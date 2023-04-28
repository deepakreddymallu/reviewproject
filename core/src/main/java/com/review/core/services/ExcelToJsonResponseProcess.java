package com.review.core.services;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.jcr.JsonItemWriter;
import org.apache.sling.engine.SlingRequestProcessor;
import org.apache.sling.models.export.spi.ModelExporter;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.day.cq.contentsync.handler.util.RequestResponseFactory;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import com.day.cq.dam.api.Asset;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMMode;
import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import java.util.TimeZone;

@Component(service = WorkflowProcess.class, property = { "process.label=Excel to Json Response Process" })
public class ExcelToJsonResponseProcess implements WorkflowProcess {

	static Logger log = LoggerFactory.getLogger(ExcelToJsonResponseProcess.class);

	@Reference
	private ModelExporter modelExporter;

	@Reference
	private RequestResponseFactory requestResponseFactory;

	@Reference
	private SlingRequestProcessor requestProcessor;

	CellType celltype = org.apache.poi.ss.usermodel.CellType.STRING;

	@Override
	public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metadataMap)
			throws WorkflowException {
		String payloadpath = (String) workItem.getWorkflow().getWorkflowData().getPayload();
		ResourceResolver resolver = workflowSession.adaptTo(ResourceResolver.class);
		Resource resource = resolver
				.resolve(payloadpath.concat("/jcr:content/root/responsivegrid/quarternarynav/excel/file/jcr:content"));
		// Asset excelAsset = resource.adaptTo(Asset.class);
		Session session = resolver.adaptTo(Session.class);

		Node fileNode = resource.adaptTo(Node.class);
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		dateformat.setTimeZone(TimeZone.getTimeZone("UTC"));

		InputStream stream = null;
		PrintWriter printWriter = null;
		File f = null;
		Workbook workbook = null;

		try {
			Node root = session.getRootNode();
			payloadpath = payloadpath.replaceFirst("/", "");
			Node jcrnode = root.getNode(
					payloadpath.concat("/jcr:content/root/responsivegrid/quarternarynav/excel/file/jcr:content"));
			JsonObject jsonOfSheets = new JsonObject();
			Binary binary = jcrnode.getProperty("jcr:data").getBinary();
			workbook = WorkbookFactory.create(binary.getStream());
			int numberOfSheets = workbook.getNumberOfSheets();
			for (int sheetnum = 0; sheetnum < numberOfSheets; sheetnum++) {
				if(sheetnum==1) {
					JsonArray sheet1Array = new JsonArray();
					sheet1Array=getSheet1Data(workbook);
					jsonOfSheets.add(Integer.toString(sheetnum), sheet1Array);
				}
				else{
				Sheet sheet = workbook.getSheetAt(sheetnum);
				int numberOfRows = sheet.getPhysicalNumberOfRows();
				log.info("ExcelToJsonResponseProcess NUMBER OF ROWS is {}", numberOfRows);
				JsonArray jsonArray = new JsonArray();
				for (int rownum = 0; rownum < numberOfRows; rownum++) {
					JsonObject jsonObject = new JsonObject();
					Row row = sheet.getRow(rownum);
					if (row == null) {
						continue;
					}
					int cellArrSize = row.getLastCellNum();
					// int dateColNo = 4;
					String[] rowValues = new String[cellArrSize];
					for (int colno = 0; colno <= cellArrSize; colno++) {
						log.info("ExceltoJsonResponseProcesscolnum is {}",colno);
						//Cell cell = row.getCell(colno);
						Cell cell = row.getCell(colno, Row.MissingCellPolicy.RETURN_NULL_AND_BLANK);
						String cellValue = StringUtils.EMPTY;
						double numericCellValue;
						Date dateCellValue;
						if (cell == null || (colno == 0 && ((cell.getCellTypeEnum() == CellType.STRING)
								? StringUtils.isBlank(cell.getStringCellValue())
								: true))) {
							if ((null != cell) && (CellType.STRING==cell.getCellTypeEnum())) {
								rowValues[colno] = cell.getStringCellValue();
								jsonObject.addProperty(Integer.toString(colno), rowValues[colno]);
							} else if ((null != cell) && (CellType.NUMERIC == cell.getCellTypeEnum())) {
								rowValues[colno] = String.valueOf(cell.getNumericCellValue());
								jsonObject.addProperty(Integer.toString(colno),
										Integer.valueOf((int)cell.getNumericCellValue()));
							}else {
								//rowValues[colno] = String.valueOf(cell.getNumericCellValue());
								jsonObject.addProperty(Integer.toString(colno),
										StringUtils.EMPTY);
							continue;}
						}
						if (cell != null) {
							switch (cell.getCellTypeEnum()) {
							case NUMERIC: {
								numericCellValue = cell.getNumericCellValue();
								jsonObject.addProperty(Integer.toString(colno), numericCellValue);
								break;
							}
							case STRING: {
								if (cell.getStringCellValue() != null) {
									cellValue = cell.getStringCellValue();
									jsonObject.addProperty(Integer.toString(colno), cellValue);
								}
								else if (cell.getDateCellValue() != null) {
									dateCellValue = cell.getDateCellValue();
									jsonObject.addProperty(Integer.toString(colno), dateformat.format(dateCellValue));
								}
								break;
							}
							default: {
								cellValue = StringUtils.EMPTY;
								break;
							}
							}
						}
						//cellValue = StringUtils.trim(cellValue);
						log.info("json key is {}", jsonObject.get(Integer.toString(colno)));
						log.info("JsonObject property of colnum{} is {}", Integer.toString(colno), jsonObject);
					}
					log.info("Final previous JsonArray is {}", jsonArray);
					jsonArray.add(jsonObject);
					
					// printWriter.write(jsonArray.toString());
					log.info("Final JsonArray is {}", jsonArray);
					log.info("Final JsonArray String is {}", jsonArray.toString());
				}
				log.info("JsonArray outside loop is {}", jsonArray.toString());

				log.info("printWriter is {}", printWriter);
				log.info("printWriter json output is {}", jsonArray.get(0).toString());
				jsonOfSheets.add(Integer.toString(sheetnum), jsonArray);
			}
		}
			File outputfile = new File("C:/Deepak/newjson.json");
			FileWriter writer = new FileWriter(outputfile);
			BufferedWriter bufferedWriter = new BufferedWriter(writer);
			printWriter = new PrintWriter(bufferedWriter);
			log.info("JsonOfSheets is {}", jsonOfSheets);
			log.info("JsonOfSheets String value is {}", jsonOfSheets.toString());
			try {
				printWriter.write(jsonOfSheets.toString());
				printWriter.close();
			} catch (JsonIOException e) {
				log.error("JsonIOException occured {}", e);
			} catch (Exception e) {
				log.error("Exception occured {}", e);
			}
		} catch (JsonIOException jsonException) {
			log.error("JSONIOException occured in ExcelToJsonResponseProcess {}", jsonException);
		} catch (IOException e) {
			log.error("IOException occured in ExcelToJsonResponseProcess {}", e);
		} catch (Exception e) {
			log.error("Exception occured in ExcelToJsonResponseProcess {}", e);
		}
	}

	public String readXml(InputStream content) {
		String readLine;
		BufferedReader br = new BufferedReader(new InputStreamReader(content));
		StringBuilder strBuilder = new StringBuilder();

		try {
			while (((readLine = br.readLine()) != null)) {
				strBuilder.append(readLine);
			}
		} catch (IOException e) {
			log.error("ExcelToJsonResponseProcess.. IOException while reading InputStream into String : {}",
					e.getMessage());
		}
		return strBuilder.toString();
	}

	public JsonArray getSheet1Data(Workbook workbook) {
		JsonArray jsonArray = new JsonArray();
		JsonArray datajson = new JsonArray();
		JsonObject datajsonobj = new JsonObject();
		Sheet sheet = workbook.getSheetAt(1);
		int rows = sheet.getPhysicalNumberOfRows();
		JsonObject jsonObject = new JsonObject();
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String[] titles2=null;
		for (int i = 0; i < rows-1; i++) {
			Row row = sheet.getRow(i);
			if (row == null) {
				continue;
			}
			else if ((i == 0) || (i == 1)) {
				// String[] titles = new String[noOfCells];
					String title = row.getCell(0).getStringCellValue();
					String titleValue = row.getCell(1).getStringCellValue();
					jsonObject.addProperty(title, titleValue);
					if(i == 1) {
					jsonArray.add(jsonObject);
					}
			} else if (i == 2) {
				int noOfCells = row.getPhysicalNumberOfCells();
				titles2 = new String[noOfCells];
				for (int k = 0; k < noOfCells; k++) {
					String title = row.getCell(k).getStringCellValue();
					titles2[k] = title;
					log.info("ExceltoJsonProcess titles arrayk is {}", titles2[k]);
				}
				log.info("ExceltoJsonProcess titles array is {},{}", titles2[0], titles2[1]);
			}
			else if (i >= 3) {
				JsonObject json = new JsonObject();
				int cells;
				if((i >= 3) && (i!=8) && (i!=7)) {
				cells = row.getLastCellNum();
				}
				else {
					cells=5;
				}
				//else {
				if((i!=7) && (i!=8)) {
					for (int f = 0; f < cells; f++) {
						log.info("row value is {}", row);
						Cell cell = row.getCell(f);
						log.info("cell value is {}", cell);
						/*
						 * String titleValue = newcell.getStringCellValue(); json.add(titleValue);
						 */
						if (cell == null || (f == 0 && ((cell.getCellTypeEnum() == CellType.STRING)
								? StringUtils.isBlank(cell.getStringCellValue())
								: true))) {
							if ((null != cell) && (CellType.STRING == cell.getCellTypeEnum())) {
								json.addProperty(titles2[f], cell.getStringCellValue());
							} else if ((null != cell) && (CellType.NUMERIC == cell.getCellTypeEnum())) {
								json.addProperty(titles2[f], Integer.valueOf((int) cell.getNumericCellValue()));
							} else {
								// rowValues[colno] = String.valueOf(cell.getNumericCellValue());
								if((i==8) || (i==7)) {
								//json.addProperty(titles2[f], StringUtils.EMPTY);
								continue;
								}
							}
						} else if (cell != null) {

							switch (cell.getCellTypeEnum()) {
							case NUMERIC: {
								json.addProperty(titles2[f], cell.getNumericCellValue());
								break;
							}
							case STRING: {
								if (cell.getStringCellValue() != null) {
									if((i==8) || (i==7)) {
										json.addProperty(sheet.getRow(i).getCell(0).getStringCellValue(), sheet.getRow(i).getCell(1).getStringCellValue());
									}
									else {
									json.addProperty(titles2[f], cell.getStringCellValue());
									}
								} else if (cell.getDateCellValue() != null) {
									json.addProperty(titles2[f], dateformat.format(cell.getDateCellValue()));
								}
								break;
							}
							default: {
								break;
							}
							}

						}
						// array.add(jsonObj);
					}
			}
					if(i<(rows-3)) {
					jsonArray.add(json);
					}
					
					//***********
					if(i >= (rows-3)) {
						int columns = sheet.getRow(i).getLastCellNum();
						Row newrow = sheet.getRow(i);
						log.info("newrownumnewrowber is {}",i);
						log.info("newrow{} is {}",i,newrow);
					for (int k = 0; k <columns; k++) {
						int cellcount = newrow.getLastCellNum();
					log.info("row value is {}", row);
					Cell cell = newrow.getCell(k);
					
					log.info("cell value is {}", cell);
					/*
					 * String titleValue = newcell.getStringCellValue(); json.add(titleValue);
					 */
					if (cell == null || (((cell.getCellTypeEnum() == CellType.STRING)
							? StringUtils.isBlank(cell.getStringCellValue())
							: true))) {
						if ((null != cell) && (CellType.STRING == cell.getCellTypeEnum())) {
							datajsonobj.addProperty("",cell.getStringCellValue());
						} else if ((null != cell) && (CellType.NUMERIC == cell.getCellTypeEnum())) {
							datajsonobj.addProperty("",Integer.valueOf((int) cell.getNumericCellValue()));
						} else {
							// rowValues[colno] = String.valueOf(cell.getNumericCellValue());
							datajsonobj.addProperty("","");
							continue;
						}
					} else if (cell != null) {

						switch (cell.getCellTypeEnum()) {
						case NUMERIC: {
							datajsonobj.addProperty("",cell.getNumericCellValue());
							break;
						}
						case STRING: {
							if (cell.getStringCellValue() != null) {
								if((i == (rows-2)) && (k==0)) {
									datajsonobj.addProperty(newrow.getCell(0).getStringCellValue(),newrow.getCell(1).getStringCellValue());}
								else if((i == (rows-2)) && (k==1)) {
								datajsonobj.addProperty(newrow.getCell(0).getStringCellValue(),newrow.getCell(1).getStringCellValue());}
								else if((i == (rows-3)) && (k==0)) {
									datajsonobj.addProperty(newrow.getCell(0).getStringCellValue(),newrow.getCell(1).getStringCellValue());}
								else if((i == (rows-3)) && (k==1)) {
									datajsonobj.addProperty(newrow.getCell(0).getStringCellValue(),newrow.getCell(1).getStringCellValue());}
							} else if (cell.getDateCellValue() != null) {
								datajsonobj.addProperty("",dateformat.format(cell.getDateCellValue()));
							}
							break;
						}
						default: {
							break;
						}
						}

					}
					// array.add(jsonObj);
			}		if(i==8) {
					jsonArray.add(datajsonobj);
			}
			}
					
					//*********
				/*	if ((i == (rows - 2))) {
						datajsonobj.addProperty("MNR", "MDR");
						datajsonobj.addProperty("MAR", "MPR"); 
					jsonArray.add(datajsonobj);
				} 
				 if (i == (rows - 1)){
					datajsonobj.addProperty("MAR", "MPR"); 
					jsonArray.add(datajsonobj);
				}*/
				//}
				//jsonArray.add(json);
			}
		}
		return jsonArray;
	}
	public static XSSFWorkbook getWorkBookFromDAM(Resource original) {
		XSSFWorkbook workbook = null;
		StringBuilder sb = new StringBuilder();
		try (InputStream inputStream = Objects.requireNonNull(original.getChild("jcr:content"))
				.adaptTo(InputStream.class)) {
			workbook = new XSSFWorkbook(inputStream);
		} catch (IOException e) {
			log.error("IOException in getWorkBookFromDAM method : ", e);
		} catch (Exception e) {
			log.error("Exception in getWorkBookFromDAM method : ", e);
		}
		return workbook;
	}

}
