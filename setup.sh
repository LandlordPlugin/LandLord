#!/bin/bash

function install_run_script {
	mkdir -p .idea/runConfigurations

	cat > .idea/runConfigurations/Server_$1.xml << EOF
<component name="ProjectRunConfigurationManager">
  <configuration name="Server ${1}" type="JarApplication" factoryName="JAR Application">
    <option name="JAR_PATH" value="\$PROJECT_DIR$/BuildTools/spigot-${1}.jar" />
    <option name="WORKING_DIRECTORY" value="\$PROJECT_DIR$/target/Testserver_${1}" />
    <option name="ALTERNATIVE_JRE_PATH" />
    <method v="2">
      <option name="Maven.BeforeRunTask" enabled="true" file="\$PROJECT_DIR$/pom.xml" goal="package" />
    </method>
  </configuration>
</component>
EOF

	echo "Installed run script. Please reopen the project!"
}

function download_deps {
	mkdir -p target/Testserver_$1/plugins

	cd target/Testserver_$1/plugins
	# download dependency plugins into right folder
	cd target/Testserver_$1/plugins
	curl -o wg.jar http://builds.enginehub.org/job/worldguard/11039/download/worldguard-legacy-7.0.0-SNAPSHOT-dist.jar
	curl -o we.jar http://builds.enginehub.org/job/worldedit/11047/download/worldedit-bukkit-7.0.0-SNAPSHOT-dist.jar
	wget -O vault.jar https://dev.bukkit.org/projects/vault/files/latest
	wget -O protocollib.jar https://dev.bukkit.org/projects/protocollib/files/latest
	wget -O eco.jar https://github.com/HimaJyun/Jecon/releases/download/2.0.1/Jecon-2.0.1.jar
	cd ..
	cd ..
	cd ..
	echo "Downloaded all dependencies"
}

function install_buildtools {
	# build spigot version with buildtools
	if [ ! -d BuildTools ]; then
	        mkdir BuildTools
	        echo "Created BuildTools directory!"
	fi

	cd BuildTools

	echo "Starting to download latest BuildTools.jar"
	curl -o BuildTools.jar https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar
	echo "BuildTools downloaded!"
	java -jar BuildTools.jar --rev $1

	echo "Downloaded spigot $1"
	cd ..
}

function accept_eula {
	echo "eula=true" > target/Testserver_$1/eula.txt
	echo "Accepted eula!"
}

# pass the version via paramater like ./setup.sh 1.13.2

# updates spigot only
if [ "$1" = "spigot" ]; then
	if [ "$#" -ne 2 ]; then
	    echo "Please add the version as second parameter!"
	    exit
	fi

	install_buildtools $2
fi

# updates dependencies only
if [ "$1" = "deps" ]; then
        if [ "$#" -ne 2 ]; then
            echo "Please add the version as second parameter!"
            exit
        fi

        download_deps $2
fi


if [ "$#" -ne 1 ]; then
    echo "Please add the version as parameter!"
    exit
fi

install_buildtools $1
#download_deps $1
#install_run_script $1
#accept_eula $1
