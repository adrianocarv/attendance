package com.attendance.backend.repository;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.attendance.backend.model.OutputView;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.WriteMode;

@Repository
public class DropboxRepository {

	@Value("${dropbox.accessToken}")
	private String ACCESS_TOKEN;

	@Value("${dropbox.rootPath}")
	private String ROOT_PATH;
	
	public void uploadOutputView(OutputView outputView) throws Exception {

		@SuppressWarnings("deprecation")
		DbxRequestConfig config = new DbxRequestConfig("dropbox/java-tutorial", "en_US");
		DbxClientV2 client = new DbxClientV2(config, ACCESS_TOKEN);

		String path = ROOT_PATH+"/"+ outputView.getEntityId() +"/"+ outputView.getName();
		
		if(outputView.isFormatExcel()){
			InputStream in = new ByteArrayInputStream(outputView.getExcelContentFile());
			client.files().uploadBuilder(path + ".xlsx").withMode(WriteMode.OVERWRITE).uploadAndFinish(in);
		}
		
		if(outputView.isFormatCSV()){
			InputStream in = new ByteArrayInputStream(outputView.getCSVContentFile());
			client.files().uploadBuilder(path + ".csv").withMode(WriteMode.OVERWRITE).uploadAndFinish(in);
		}
	}
}


