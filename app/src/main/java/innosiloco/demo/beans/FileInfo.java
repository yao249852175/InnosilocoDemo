package innosiloco.demo.beans;

/**
 * 
 * @ClassName: FileInfo
 * @Description: 文件信息实体类
 * @author:ZCS
 * @date: 2016年1月5日 下午2:21:27
 */
public class FileInfo {
	public String fileName;

	public String filePath;

	public long fileSize;

	public boolean IsDir;

	public int Count;

	public long ModifiedDate;

	public boolean Selected;

	public boolean canRead;

	public boolean canWrite;

	public boolean isHidden;

	public long dbId;
}
