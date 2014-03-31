package de.ifgi.lodum.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.Logger;

public class FileUtils {

//	protected static Logger log =Logger.getLogger(this.getClass().getName());

	public static void zipFolder(String srcFolder, String destZipFile) throws Exception {
		ZipOutputStream zip = null;
		FileOutputStream fileWriter = null;

		fileWriter = new FileOutputStream(destZipFile);
		zip = new ZipOutputStream(fileWriter);

		addFolderToZip("", srcFolder, zip);
		zip.flush();
		zip.close();
	}

	public static void zipFiles(ArrayList<File> files, String destZipFile) throws Exception {
		if(files.size()>0){
			ZipOutputStream zip = null;
			FileOutputStream fileWriter = null;

			fileWriter = new FileOutputStream(destZipFile);
			zip = new ZipOutputStream(fileWriter);
			for(File f:files){
				addFileToZip("",f.getAbsolutePath(),zip );
			}

			zip.flush();
			zip.close();
//			this.log.info("Zipped "+files.size()+" file(s) to "+ destZipFile);
		}
	
	}

	private static void addFileToZip(String path, String srcFile, ZipOutputStream zip)
	throws Exception {

		File folder = new File(srcFile);
		if (folder.isDirectory()) {
			addFolderToZip(path, srcFile, zip);
		} else {
			byte[] buf = new byte[1024];
			int len;
			FileInputStream in = new FileInputStream(srcFile);
			zip.putNextEntry(new ZipEntry(path + "/" + folder.getName()));
			while ((len = in.read(buf)) > 0) {
				zip.write(buf, 0, len);
			}
		}
	}

	private static void addFolderToZip(String path, String srcFolder, ZipOutputStream zip)
	throws Exception {
		File folder = new File(srcFolder);

		for (String fileName : folder.list()) {
			if (path.equals("")) {
				addFileToZip(folder.getName(), srcFolder + "/" + fileName, zip);
			} else {
				addFileToZip(path + "/" + folder.getName(), srcFolder + "/" + fileName, zip);
			}
		}
	}

	public boolean deleteDirectory(File path) {
		if( path.exists() ) {
			File[] files = path.listFiles();
			for(int i=0; i<files.length; i++) {
				if(files[i].isDirectory()) {
					deleteDirectory(files[i]);
				}
				else {
					files[i].delete();
				}
			}
		}
		return( path.delete() );
	}

}
