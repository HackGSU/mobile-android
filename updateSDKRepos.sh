#!/usr/bin/env bash


echo "Starting update SDK bash script";

mkdir "$ANDROID_HOME/licenses" || true
echo -e "\n8933bad161af4178b1185d1a37fbf41ea5269c55" > "$ANDROID_HOME/licenses/android-sdk-license"
echo -e "\n84831b9409646a918e30573bab4c9c91346d8abd" > "$ANDROID_HOME/licenses/android-sdk-preview-license"

echo "y" | $ANDROID_HOME/tools/android update sdk --no-ui
echo "y" | $ANDROID_HOME/tools/android update sdk -u -a -t `$ANDROID_HOME/tools/android list sdk --all | grep -m 1 Support | sed 's/.\([0-9]*\).*/\1/'`;

echo "Finished update SDK bash script";