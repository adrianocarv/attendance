package com.attendance.backend.util;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.attendance.backend.model.OutputView;
import com.opencsv.CSVWriter;


public class FileFormatUtil 
{
    public static byte[] getExcelContentFile(OutputView outputView) throws Exception {
        //Blank workbook
        XSSFWorkbook workbook = new XSSFWorkbook(); 
         
        //Create a blank sheet
        XSSFSheet sheet = workbook.createSheet(outputView.getName());
        
        Map<String, Object[]> data = outputView.getExcelData();

        //Iterate over data and write to sheet
        Set<String> keyset = data.keySet();
        int rownum = 0;
        for (String key : keyset)
        {
            Row row = sheet.createRow(rownum++);
            Object [] objArr = data.get(key);
            int cellnum = 0;
            for (Object obj : objArr)
            {
               Cell cell = row.createCell(cellnum++);
               if(obj instanceof String)
                    cell.setCellValue((String)obj);
                else if(obj instanceof Integer)
                    cell.setCellValue((Integer)obj);
            }
        }

    	ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
    	out.close();
        workbook.close();
        
        return out.toByteArray();
    }
    
    public static byte[] getCSVContentFile(OutputView outputView) throws Exception {

    	ByteArrayOutputStream out = new ByteArrayOutputStream();
    	CSVWriter writer = new CSVWriter(new OutputStreamWriter(out));

        writer.writeAll(outputView.getCSVData(), true);
        
        writer.close();
    	out.close();

        return out.toByteArray();
    }
    
}