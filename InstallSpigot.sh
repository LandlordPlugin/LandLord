#!/bin/bash

if [ ! -d BuildTools ]; then
	mkdir BuildTools
	echo "Created directory!"
fi

cd BuildTools

echo "Starting to download latest BuildTools.jar"
curl -o BuildTools.jar https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar
echo "BuildTools downloaded!"

echo "Enter your versions:"

versions="latest"

for i in ${versions} ; do
	java -jar BuildTools.jar --rev ${i}
echo
done

echo "Successfully downloaded all spigot versions!"

echo "Installing IntelliJ Configuration Files"

cd ..
curl -o Serverx.xml http://cloud.daichendt.one/index.php/s/2LAJ6PYi7zCQnlS/download

mkdir .idea/runConfigurations

if [ ! "$1" = "-nc" ]; then
    for i in ${versions}; do
	    old="%i"
	    cp "Serverx.xml" .idea/runConfigurations/Serverx${i}.xml
	    sed -i "s@$old@$i@" .idea/runConfigurations/Serverx${i}.xml
	    old="%a"
	    path="$(pwd)/$line"
        sed -i "s@$old@$path@" .idea/runConfigurations/Serverx${i}.xml
        echo "Installed start config files for IntelliJ"
    done
fi

rm Serverx.xml

echo "Done installing the latest versions of spigot :)"
