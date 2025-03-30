package io.antoon.mc.ccc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.List;
import java.util.Properties;

public class CCCConfig {
	String apiUrl;
	String apiSecret;

	List<String> skullOwners;

	public boolean playerHeadsAvailable() {
		return skullOwners != null && !skullOwners.isEmpty();
	}

	public String getRandomSkullOwner() {
		if (!playerHeadsAvailable())
			return null;

		return skullOwners.get(CCCMain.cccRandom.nextInt(skullOwners.size()));
	}

	// We don't want feature toggles but this is experimental, and we might want to be able to disable
	public boolean customEndermenBlocksEnabled;

	public boolean apiEnabled() {
		return apiUrl.length() != 0;
	}

	private static Logger LOGGER = LogManager.getLogger("CCC-Config");

	CCCConfig(Properties props) {
		apiUrl = props.getProperty("api-url", "");
		apiSecret = props.getProperty("api-secret", "");

		customEndermenBlocksEnabled = props.getProperty("custom-enderman-block", "true").equals("true");
	}

	static CCCConfig load(String path) {
		File configFile = new File(path);
		if (!configFile.exists()) {
			try {
				writeDefaultConfig(configFile);
			}
			catch (IOException e) {
				LOGGER.warn("Couldn't write default config file", e);
			}
		}

		Properties props = new Properties();
		try {
			props.load(new FileInputStream(configFile));
		}
		catch (IOException e) {
			LOGGER.warn("Couldn't read the config file", e);
		}

		return new CCCConfig(props);
	}

	private static void writeDefaultConfig(File configFile) throws IOException {
		File dir = configFile.getParentFile();

		if (!dir.exists()) {
			if (!dir.mkdirs()) {
				throw new IOException("Could not create parent directories");
			}
		} else if (!dir.isDirectory()) {
			throw new IOException("The parent file is not a directory");
		}

		try (Writer writer = new FileWriter(configFile)) {
			writer.write("# This is the configuration file for CC-Custom.\n");
			writer.write("# Configuration options can be found at https://github.com/ntoonio/cc-custom/blob/master/CONFIG.md\n");
			writer.write("# By default, this file will be empty except for this notice.\n");
		}
	}
}
