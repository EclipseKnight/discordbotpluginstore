package discordbotpluginstore.net.database;

import java.io.File;

import discordbotpluginstore.net.Launcher;
import discordbotpluginstore.net.logger.Logger;
import discordbotpluginstore.net.logger.Logger.Level;
import io.jsondb.JsonDBTemplate;
import io.jsondb.events.CollectionFileChangeListener;

public class JsonDB {

	public static String dbFilesLocation = Launcher.uwd + File.separator + "plugins" + File.separator + "discordbotpluginstore" + File.separator + "database";
	private static String baseScanPackage = "discordbotpluginstore.net.database";
	public static JsonDBTemplate database;
	
	public static void init() {
		if (new File(dbFilesLocation).mkdirs()) {
			Logger.log(Level.INFO, "Database directory created...");
		}
		
		database = new JsonDBTemplate(dbFilesLocation, baseScanPackage);
		
		if (!database.collectionExists(MCPlayer.class)) {
			database.createCollection(MCPlayer.class);
		}
		
		database.addCollectionFileChangeListener(new CollectionFileChangeListener() {
			
			@Override
			public void collectionFileModified(String collectionName) {
				database.reloadCollection(collectionName);
				Logger.log(Level.WARN, "Collection File Modified: " + collectionName +"\nReloaded Collection.");
			}
			
			@Override
			public void collectionFileDeleted(String collectionName) {
				database.reLoadDB();
				Logger.log(Level.WARN, "Collection File Deleted: " + collectionName +"\nReloaded Database.");
			}
			
			@Override
			public void collectionFileAdded(String collectionName) {
				database.reLoadDB();
				Logger.log(Level.WARN, "Collection File Added: " + collectionName +"\nReloaded Database.");
			}
		});
	}
}
