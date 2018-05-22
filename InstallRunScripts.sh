#!/bin/bash
versions="1.8.8 1.9.4 1.10.2 1.11.2 1.12.2"

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
