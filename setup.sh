#!/bin/bash

function install_run_scripts {
	mkdir -p .idea/runConfigurations

	cat > .idea/runConfigurations/Server_1.13.2.xml << EOF
<component name="ProjectRunConfigurationManager">
  <configuration name="Server 1.13.2" type="JarApplication" factoryName="JAR Application">
    <option name="JAR_PATH" value="\$PROJECT_DIR$/BuildTools/spigot-1.13.2.jar" />
    <option name="WORKING_DIRECTORY" value="\$PROJECT_DIR$/LandLord-latest/target/Testserver_1.13.2" />
    <option name="ALTERNATIVE_JRE_PATH" />
    <method v="2">
      <option name="Maven.BeforeRunTask" enabled="true" file="\$PROJECT_DIR$/pom.xml" goal="package" />
    </method>
  </configuration>
</component>
EOF
cat > .idea/runConfigurations/Server_1.12.2.xml << EOF
<component name="ProjectRunConfigurationManager">
  <configuration name="Server 1.12.2" type="JarApplication" factoryName="JAR Application">
    <option name="JAR_PATH" value="\$PROJECT_DIR$/BuildTools/spigot-1.12.2.jar" />
    <option name="WORKING_DIRECTORY" value="\$PROJECT_DIR$/LandLord-legacy/target/Testserver_1.12.2" />
    <option name="ALTERNATIVE_JRE_PATH" />
    <method v="2">
      <option name="Maven.BeforeRunTask" enabled="true" file="\$PROJECT_DIR$/pom.xml" goal="package" />
    </method>
  </configuration>
</component>
EOF
	echo "Installed run script. Please reopen the project!"
}

function download_deps_1_14 {
	mkdir -p LandLord-latest/target/Testserver_1.14.2/plugins

	cd LandLord-latest/target/Testserver_1.14.2/plugins
	# download dependency plugins into right folder
	curl -o wg.jar http://builds.enginehub.org/job/worldguard/12048/download/worldguard-legacy-7.0.0-SNAPSHOT-dist.jar
	curl -o we.jar http://builds.enginehub.org/job/worldedit/12054/download/worldedit-bukkit-7.0.0-SNAPSHOT-dist.jar
	wget -O vault.jar https://dev.bukkit.org/projects/vault/files/latest
	wget -O protocollib.jar https://dev.bukkit.org/projects/protocollib/files/latest
	wget -O eco.jar https://github.com/HimaJyun/Jecon/releases/download/2.1.0/Jecon-2.1.0.jar
	cd ..
	cd ..
	cd ..
	cd ..
	echo "Downloaded all dependencies for 1.14.2"
}

function download_deps_1_13 {
	mkdir -p LandLord-latest/target/Testserver_1.13.2/plugins

	cd LandLord-latest/target/Testserver_1.13.2/plugins
	# download dependency plugins into right folder
	curl -o wg.jar http://builds.enginehub.org/job/worldguard/11622/download/worldguard-legacy-7.0.0-SNAPSHOT-dist.jar
	curl -o we.jar http://builds.enginehub.org/job/worldedit/11635/download/worldedit-bukkit-7.0.0-SNAPSHOT-dist.jar
	wget -O vault.jar https://dev.bukkit.org/projects/vault/files/latest
	wget -O protocollib.jar https://dev.bukkit.org/projects/protocollib/files/latest
	wget -O eco.jar https://github.com/HimaJyun/Jecon/releases/download/2.0.2/Jecon-2.0.2.jar
	cd ..
	cd ..
	cd ..
	cd ..
	echo "Downloaded all dependencies for 1.13.2"
}

function download_deps_1_12 {
	mkdir -p LandLord-legacy/target/Testserver_1.12.2/plugins

	cd LandLord-legacy/target/Testserver_1.12.2/plugins
	# download dependency plugins into right folder
	wget -O wg.jar https://dev.bukkit.org/projects/worldguard/files/2610618/download
	wget -O we.jar https://dev.bukkit.org/projects/worldedit/files/2597538/download
	wget -O vault.jar https://dev.bukkit.org/projects/vault/files/latest
	wget -O protocollib.jar https://dev.bukkit.org/projects/protocollib/files/latest
	wget -O eco.jar https://github.com/HimaJyun/Jecon/releases/download/2.0.2/Jecon-2.0.2.jar
	cd ..
	cd ..
	cd ..
	cd ..
	echo "Downloaded all dependencies for 1.12.2"
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

function accept_eulas {
	echo "eula=true" > LandLord-legacy/target/Testserver_1.12.2/eula.txt
	echo "eula=true" > LandLord-latest/target/Testserver_1.14.2/eula.txt
	echo "Accepted eula!"
}


versions=(1.12.2 1.14.2)

for version in ${versions}; do
    install_buildtools ${version}
done

download_deps_1_12
download_deps_1_14
install_run_scripts
accept_eulas
