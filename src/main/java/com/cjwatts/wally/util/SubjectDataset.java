package com.cjwatts.wally.util;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.FileType;
import org.apache.commons.vfs2.FileTypeSelector;
import org.apache.commons.vfs2.VFS;
import org.openimaj.data.dataset.ReadableGroupDataset;
import org.openimaj.data.dataset.VFSGroupDataset;
import org.openimaj.data.dataset.VFSListDataset;
import org.openimaj.data.identity.Identifiable;
import org.openimaj.io.InputStreamObjectReader;
import org.openimaj.io.ObjectReader;

import com.cjwatts.wally.analysis.Subject;
import com.cjwatts.wally.analysis.SubjectFactory;

/**
 * Implementation adapted from {@link VFSGroupDataset}
 * 
 * @author Chris Watts (cw17g12@ecs.soton.ac.uk), Jonathon Hare (jsh2@ecs.soton.ac.uk)
 */
public class SubjectDataset<INSTANCE>
				extends ReadableGroupDataset<Subject, VFSListDataset<INSTANCE>, INSTANCE, FileObject>
				implements Identifiable {

	private Map<Subject, VFSListDataset<INSTANCE>> files = new LinkedHashMap<>();
	private Map<Subject, FileObject> directoryInfo = new LinkedHashMap<>();
	private FileObject base;

	/**
	 * Construct a grouped dataset from any virtual file system source (local
	 * directory, remote zip file, etc). Only the child directories under the
	 * given path will be used to create groups; the contents of any
	 * sub-directories will be merged automatically. Only directories with
	 * readable items as children will be included in the resultant dataset.
	 * 
	 * @see "http://commons.apache.org/proper/commons-vfs/filesystems.html"
	 * @param path
	 *            the file system path or uri. See the Apache Commons VFS2
	 *            documentation for all the details.
	 * @param reader
	 *            the {@link InputStreamObjectReader} that reads the data from
	 *            the VFS
	 * @throws FileSystemException
	 *             if an error occurs accessing the VFS
	 */
	public SubjectDataset(final String path, final InputStreamObjectReader<INSTANCE> reader) throws FileSystemException {
		this(path, new VFSListDataset.FileObjectISReader<INSTANCE>(reader));
	}

	/**
	 * Construct a grouped dataset from any virtual file system source (local
	 * directory, remote zip file, etc). Only the child directories under the
	 * given path will be used to create groups; the contents of any
	 * sub-directories will be merged automatically. Only directories with
	 * readable items as children will be included in the resultant dataset.
	 * 
	 * @see "http://commons.apache.org/proper/commons-vfs/filesystems.html"
	 * @param path
	 *            the file system path or uri. See the Apache Commons VFS2
	 *            documentation for all the details.
	 * @param reader
	 *            the {@link InputStreamObjectReader} that reads the data from
	 *            the VFS
	 * @throws FileSystemException
	 *             if an error occurs accessing the VFS
	 */
	public SubjectDataset(final String path, final ObjectReader<INSTANCE, FileObject> reader) throws FileSystemException
	{
		super(reader);

		final FileSystemManager fsManager = VFS.getManager();
		base = fsManager.resolveFile(path);

		final FileObject[] folders = base.findFiles(new FileTypeSelector(FileType.FOLDER));

		Arrays.sort(folders, new Comparator<FileObject>() {
			@Override
			public int compare(FileObject o1, FileObject o2) {
				return o1.getName().toString().compareToIgnoreCase(o2.getName().toString());
			}
		});

		for (final FileObject folder : folders) {
			if (folder.equals(base))
				continue;
			
			// Create new subject from XML descriptor
			Subject s = SubjectFactory.xmlBackedSubject(folder.getName().getPath(), folder.getName().getBaseName());
			if (!s.isLoaded())
				continue;
			
			directoryInfo.put(s, folder);
			final VFSListDataset<INSTANCE> list = new VFSListDataset<INSTANCE>(folder.getName().getURI(), reader);

			if (list.size() > 0)
				files.put(s, list);
		}
	}

	/**
	 * Get the underlying file descriptors of the directories that form the
	 * groups of the dataset
	 * 
	 * @return the array of file objects
	 */
	public Map<Subject, FileObject> getGroupDirectories() {
		return directoryInfo;
	}

	/**
	 * Get the underlying file descriptor for a particular group in the dataset.
	 * 
	 * @param key
	 *            key of the group
	 * 
	 * @return the file object corresponding to the instance
	 */
	public FileObject getFileObject(Subject key) {
		return directoryInfo.get(key);
	}

	@Override
	public String toString() {
		return String.format("%s(%d groups with a total of %d instances)", this.getClass().getName(), this.size(),
				this.numInstances());
	}

	@Override
	public Set<Entry<Subject, VFSListDataset<INSTANCE>>> entrySet() {
		return files.entrySet();
	}

	@Override
	public String getID() {
		return base.getName().getBaseName();
	}
	
}
