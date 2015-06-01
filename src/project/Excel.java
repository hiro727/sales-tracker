package project;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

public class Excel {
	
	private static Workbook w = new HSSFWorkbook();
	private static CellStyle border, borderHeader;
	private static Font bold;
	public Excel() {
		bold = w.createFont();
		//bold.setFontHeightInPoints((short) 24);
		bold.setBoldweight(Font.BOLDWEIGHT_BOLD);
		
		border = w.createCellStyle();
		border.setBorderBottom(CellStyle.BORDER_THIN);
		border.setBorderTop(CellStyle.BORDER_THIN);
		border.setBorderRight(CellStyle.BORDER_THIN);
		border.setBorderLeft(CellStyle.BORDER_THIN);
		
		borderHeader = w.createCellStyle();
		borderHeader.setBorderBottom(CellStyle.BORDER_THIN);
		borderHeader.setBorderTop(CellStyle.BORDER_THIN);
		borderHeader.setBorderRight(CellStyle.BORDER_THIN);
		borderHeader.setBorderLeft(CellStyle.BORDER_THIN);
		borderHeader.setAlignment(CellStyle.ALIGN_CENTER);
		borderHeader.setFillPattern(CellStyle.SOLID_FOREGROUND);
		borderHeader.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());//YELLOW.getIndex());
		borderHeader.setFont(bold);
	}
	
	
	public static void save(String file_path, String title, JScrollPane sp) {
		
		//get table and list of row names from scroll pane
		JTable tbl = (JTable) sp.getViewport().getComponent(0);
		//JList list = (JList) sp.getRowHeader().getComponent(0);
		int row_count = tbl.getRowCount();
		int col_count = tbl.getColumnCount();
		
		Sheet sheet = w.createSheet();
		
		//create the first row
		Row row = sheet.createRow(0);
		
		Cell cell = row.createCell(0);
		
		//merged region for title
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, tbl.getRowCount()));
		
		cell.setCellValue(title);
		
		row = sheet.createRow(4);
		
		//enter column names of the table
		for (int i = 0; i < col_count; i++) {
			cell = row.createCell(i + 3);
			cell.setCellStyle(borderHeader);
			cell.setCellValue(tbl.getColumnName(i));
		}
		//enter row names of the table
		for (int i = 0; i < row_count; i++) {
			row  = sheet.createRow(i + 5);
			cell = row.createCell(2);
			cell.setCellStyle(borderHeader);
			cell.setCellValue("row "+i);
			
			//enter result values in the corresponding cells
			for (int j = 0; j < col_count; j++) {
				cell = row.createCell(j + 3);
				cell.setCellStyle(border);
				try{
					cell.setCellValue(Integer.parseInt(tbl.getValueAt(i, j).toString()));
				} catch (NullPointerException e){
					//if the value does not exist, enter 0
					cell.setCellValue(0);
				}
			}
		}
		try {
			//save this file into .xls file
			File f = new File(file_path);
			FileOutputStream fo = new FileOutputStream(f);
			w.write(fo);
			fo.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}