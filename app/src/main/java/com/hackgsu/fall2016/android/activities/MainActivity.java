package com.hackgsu.fall2016.android.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.hackgsu.fall2016.android.BuildConfig;
import com.hackgsu.fall2016.android.DataStore;
import com.hackgsu.fall2016.android.HackGSUApplication;
import com.hackgsu.fall2016.android.R;
import com.hackgsu.fall2016.android.events.OpeningCeremoniesRoomNumberUpdateEvent;
import com.hackgsu.fall2016.android.events.RequestAMentorEvent;
import com.hackgsu.fall2016.android.fragments.*;
import com.hackgsu.fall2016.android.model.Announcement;
import com.hackgsu.fall2016.android.utils.BusUtils;
import com.ncapdevi.fragnav.FragNavController;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;
import org.greenrobot.eventbus.Subscribe;
import org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity
		implements NavigationView.OnNavigationItemSelectedListener, OnTabSelectListener, OnTabReselectListener, AppBarLayout.OnOffsetChangedListener {
	public static final String APP_SHORTCUT_INTENT_KEY = "app_shortcut";
	public static final String HIGHLIGHT_ANNOUNCEMENT  = "highlight_announcement";
	private boolean           announcementsFilteredByBookmarked;
	private AppBarLayout      appbar;
	private BottomBar         bottomBar;
	private FragNavController fragNavController;
	private boolean           hasScrolled;
	private View              headerView;
	private BaseFragment      lastFragment;
	private BaseFragment      lastHomeFragment;
	private Menu              menu;
	private NavigationView    navigationView;
	private TextView          openingCeremoniesRoomNumber;
	private Toolbar           toolbar;

	@Override protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
		navigationView.setVerticalScrollBarEnabled(false);
		navigationView.getMenu().findItem(R.id.nav_version).setTitle(String.format("Version: %s", BuildConfig.VERSION_NAME));

		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
																	   drawer,
																	   toolbar,
																	   R.string.navigation_drawer_open,
																	   R.string.navigation_drawer_close);
		drawer.addDrawerListener(toggle);
		toggle.syncState();
		BusUtils.register(this);
	}

	@Override public void onOffsetChanged (AppBarLayout appBarLayout, int verticalOffset) {
		appBarLayout.setExpanded(false, false);
	}

	@Override public void onBackPressed () {
		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		if (drawer.isDrawerOpen(GravityCompat.START)) {
			drawer.closeDrawer(GravityCompat.START);
		}
		else if (lastFragment.onBackPressed()) {
			//noinspection UnnecessaryReturnStatement
			return;
		}
		else if (lastFragment.equals(lastHomeFragment)) {
			super.onBackPressed();
		}
		else {
			handleAction(R.id.nav_home);
		}
	}

	@Override public boolean onCreateOptionsMenu (Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);
		this.menu = menu;
		setMenuItemVisibility(R.id.scroll_to_now, false);
		setMenuItemVisibility(R.id.filter_bookmarked_announcements, true);
		menu.findItem(R.id.mute_notifications)
			.setIcon(HackGSUApplication.areNotificationsEnabled(getApplicationContext()) ? R.drawable.ic_bell : R.drawable.ic_bell_off);
		return true;
	}

	@Override public boolean onOptionsItemSelected (MenuItem item) {
		int id = item.getItemId();

		if (id == R.id.mute_notifications) {
			boolean enabled = HackGSUApplication.areNotificationsEnabled(getApplicationContext());
			HackGSUApplication.setNotificationsEnabled(getApplicationContext(), enabled = !enabled);
			item.setIcon(enabled ? R.drawable.ic_bell : R.drawable.ic_bell_off);
			item.setTitle(enabled ? R.string.action_mute_notifications : R.string.action_unmute_notifications);
		}
		else if (id == R.id.scroll_to_now) {
			if (lastFragment instanceof ScheduleFragment) { ((ScheduleFragment) lastFragment).getScheduleRecyclerView().showNowRow(); }
		}
		else if (id == R.id.filter_bookmarked_announcements) {
			announcementsFilteredByBookmarked = !announcementsFilteredByBookmarked;
			item.setIcon(announcementsFilteredByBookmarked ? R.drawable.ic_bookmarked_items : R.drawable.ic_bookmarked_items_off);
			if (lastFragment instanceof AnnouncementsFragment) {
				((AnnouncementsFragment) lastFragment).setShowOnlyBookmarked(announcementsFilteredByBookmarked);
			}
		}

		return super.onOptionsItemSelected(item);
	}

	@SuppressWarnings ("StatementWithEmptyBody") @Override public boolean onNavigationItemSelected (MenuItem item) {
		handleAction(item.getItemId());

		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawer.closeDrawer(GravityCompat.START);
		return true;
	}

	@Override public void onTabSelected (@IdRes int tabId) {
		handleAction(tabId);
	}

	@Override public void onTabReSelected (@IdRes int tabId) {
		lastFragment.onReselected();
	}

	@Override protected void onStart () {
		super.onStart();

		appbar = (AppBarLayout) findViewById(R.id.appbar);

		navigationView = (NavigationView) findViewById(R.id.nav_view);
		navigationView.setNavigationItemSelectedListener(this);
		navigationView.setCheckedItem(R.id.nav_home);

		if (navigationView.getHeaderCount() > 0) {
			navigationView.removeHeaderView(navigationView.getHeaderView(0));
		}
		if (HackGSUApplication.getDateTimeOfHackathon().isAfter(new LocalDateTime(System.currentTimeMillis()))) {
			headerView = navigationView.inflateHeaderView(R.layout.opening_ceremonies_nav_header);
			final Timer timer = new Timer();
			TimerTask timerTask = new TimerTask() {
				@Override public void run () {
					HackGSUApplication.runOnUI(new Runnable() {
						@Override public void run () {
							try {
								if (HackGSUApplication.getDateTimeOfHackathon().isAfter(new LocalDateTime(System.currentTimeMillis()))) {
									TextView openingCeremoniesIn = (TextView) headerView.findViewById(R.id.opening_ceremonies_in);

									openingCeremoniesRoomNumber = (TextView) headerView.findViewById(R.id.opening_ceremonies_room_number);

									String openingCeremoniesInString = HackGSUApplication.toHumanReadableRelative(HackGSUApplication.getDateTimeOfHackathon(),
																												  true,
																												  false);
									openingCeremoniesInString = String.format("Opening Ceremonies \n%s",
																			  openingCeremoniesInString.replaceFirst("In", "in"));
									openingCeremoniesIn.setText(openingCeremoniesInString);
									openingCeremoniesRoomNumber.setText(HackGSUApplication.isNullOrEmpty(DataStore.getOpeningCeremoniesRoomNumber())
																		? ""
																		: DataStore.getOpeningCeremoniesRoomNumber());
								}
								else {
									if (navigationView.getHeaderCount() > 0) { navigationView.removeHeaderView(navigationView.getHeaderView(0)); }
									headerView = navigationView.inflateHeaderView(R.layout.nav_header_main);
									timer.cancel();
								}
							} catch (Exception ignored) {}
						}
					});
				}
			};
			timer.scheduleAtFixedRate(timerTask, 0, 1000);
		}
		else {
			headerView = navigationView.inflateHeaderView(R.layout.nav_header_main);
		}

		if (headerView != null) {
			headerView.setOnLongClickListener(new View.OnLongClickListener() {
				@Override public boolean onLongClick (View v) {
					HackGSUApplication.runOnUI(HackGSUApplication.getUrlRunnable(getApplicationContext(),
																				 "https://randomuser999.github.io/pantherHack_Space_Invaders_Sponsors/",
																				 true));
					return true;
				}
			});
		}

		final List<Fragment> fragments = new ArrayList<>();

		fragments.add(new AnnouncementsFragment());
		fragments.add(new ScheduleFragment());
		fragments.add(new FacilityMapFragment());
		fragments.add(new MentorsFragment());
		fragments.add(new SponsorsFragment());

		fragNavController = new FragNavController(getSupportFragmentManager(), R.id.fragment_frame, fragments);
		fragNavController.setNavListener(new FragNavController.NavListener() {
			@Override public void onTabTransaction (Fragment fragment, int index) {
				BaseFragment baseFragment = null;
				if (fragment instanceof BaseFragment) {
					baseFragment = ((BaseFragment) fragment);
				}

				if (baseFragment == null) { return; }

				lastFragment = baseFragment;
				if (index < 3) { lastHomeFragment = baseFragment; }
				setTitle(baseFragment.getTitle());

				HackGSUApplication.delayRunnableOnUI(500, new Runnable() {
					@Override public void run () {
						if (!hasScrolled && getIntent().hasExtra(HIGHLIGHT_ANNOUNCEMENT) && lastHomeFragment instanceof AnnouncementsFragment) {
							Announcement announcementToHighlight = (Announcement) getIntent().getSerializableExtra(HIGHLIGHT_ANNOUNCEMENT);
							((AnnouncementsFragment) lastHomeFragment).highlightAnnouncement(announcementToHighlight);
							hasScrolled = true;
						}
					}
				});
			}

			@Override public void onFragmentTransaction (Fragment fragment) {
			}
		});
		fragNavController.switchTab(FragNavController.TAB1);

		bottomBar = (BottomBar) findViewById(R.id.bottomBar);
		bottomBar.setOnTabSelectListener(this);
		bottomBar.setOnTabReselectListener(this);

		String appShortcut = getIntent().getStringExtra(APP_SHORTCUT_INTENT_KEY);
		if (appShortcut != null) {
			if (appShortcut.equals(getString(R.string.app_shortcut_request_a_mentor))) {
				handleAction(R.id.nav_mentors);
				BusUtils.post(new RequestAMentorEvent());
			}
			getIntent().removeExtra(APP_SHORTCUT_INTENT_KEY);
		}
	}

	@Override protected void onResume () {
		super.onResume();

		View devModeTV = findViewById(R.id.is_in_dev_mode_msg);
		if (devModeTV != null && HackGSUApplication.isInDevMode(this)) { devModeTV.setVisibility(View.VISIBLE); }
		else if (devModeTV != null) { devModeTV.setVisibility(View.GONE); }
		getSupportFragmentManager().beginTransaction().replace(R.id.fragment_frame, lastFragment).commitNow();
	}

	@Subscribe public void onEvent (OpeningCeremoniesRoomNumberUpdateEvent openingCeremoniesRoomNumberUpdateEvent) {
		openingCeremoniesRoomNumber.setText(DataStore.getOpeningCeremoniesRoomNumber());
	}

	private void handleAction (@IdRes int id) {
		@ColorRes int color = R.color.colorPrimary;

		setMenuItemVisibility(R.id.scroll_to_now, false);
		setMenuItemVisibility(R.id.filter_bookmarked_announcements, false);
		appbar.removeOnOffsetChangedListener(MainActivity.this);

		switch (id) {
			case R.id.nav_home:
				showBottomBar();
				bottomBar.selectTabAtPosition(lastHomeFragment.getTabIndex());
				lastHomeFragment.selectTab(fragNavController);
				color = lastHomeFragment.getPrimaryColor();
				navigationView.setCheckedItem(R.id.nav_home);
				break;
			case R.id.tab_announcements:
				fragNavController.switchTab(FragNavController.TAB1);
				color = R.color.announcementsPrimary;
				MenuItem menuItem = setMenuItemVisibility(R.id.filter_bookmarked_announcements, true);
				if (menuItem != null) { menuItem.setIcon(R.drawable.ic_bookmarked_items_off); }
				announcementsFilteredByBookmarked = false;
				break;
			case R.id.tab_schedule:
				fragNavController.switchTab(FragNavController.TAB2);
				color = R.color.schedulePrimary;
				setMenuItemVisibility(R.id.scroll_to_now, true);
				break;
			case R.id.tab_facility_map:
				fragNavController.switchTab(FragNavController.TAB3);
				color = R.color.facilityMapPrimary;
				break;
			case R.id.nav_mentors:
				fragNavController.switchTab(FragNavController.TAB4);
				color = R.color.mentorsPrimary;
				hideBottomBar();
				navigationView.setCheckedItem(R.id.nav_mentors);
				appbar.setExpanded(false, false);
				appbar.addOnOffsetChangedListener(MainActivity.this);
				break;
			case R.id.nav_sponsors:
				fragNavController.switchTab(FragNavController.TAB5);
				color = R.color.sponsorsPrimary;
				navigationView.setCheckedItem(R.id.nav_sponsors);
				hideBottomBar();
				appbar.setExpanded(false, false);
				appbar.addOnOffsetChangedListener(MainActivity.this);
				break;
			case R.id.nav_hack_gsu_site:
				HackGSUApplication.openWebUrl(this, "http://hackgsu.com/", false);
				break;
			case R.id.nav_hack_gsu_slack:
				HackGSUApplication.openWebUrl(this,
											  "https://hackgsu-spring17.slack.com/shared_invite/MTYyMjE3OTk2NjI2LTE0OTA4OTMzNzUtNmUxZWIyODA5Mg",
											  false);
				break;
			case R.id.nav_prizes:
				HackGSUApplication.openWebUrl(this, "https://hackgsu-spring-2017.devpost.com/#prizes", false);
				break;
			case R.id.nav_about:
				Intent aboutPageIntent = new Intent(this, AboutPageActivity.class);
				startActivity(aboutPageIntent);
				break;
			case R.id.nav_code_of_conduct:
				HackGSUApplication.openWebUrl(this,
											  "https://docs.google.com/gview?embedded=true&url=static.mlh.io/docs/mlh-code-of-conduct.pdf",
											  true);
				break;
			case R.id.nav_send_feedback:
				HackGSUApplication.openWebUrl(this, "https://sri40.typeform.com/to/QTFwTX", true);
				break;
			default:
				break;
		}
		appbar.setBackgroundResource(color);
	}

	@Nullable private MenuItem setMenuItemVisibility (@IdRes int id, boolean visible) {
		MenuItem menuItem = null;
		if (menu != null) {
			menuItem = menu.findItem(id);
			if (menuItem != null) { menuItem.setVisible(visible); }
		}
		return menuItem;
	}

	private void showBottomBar () {
		bottomBar.setVisibility(View.VISIBLE);
		bottomBar.animate().translationY(0).setDuration(500).start();
	}

	private void hideBottomBar () {
		bottomBar.animate().translationY(bottomBar.getHeight()).setDuration(500).withEndAction(new TimerTask() {
			@Override public void run () {
				bottomBar.setVisibility(View.GONE);
			}
		}).start();
	}
}
