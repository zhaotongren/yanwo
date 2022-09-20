package com.yanwo.common.utils.poi;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.*;

/**
 * excel 解析类
 *
 * @author Dax
 * @since 15 :27  2018/5/17
 */
public final class ExcelReader {

    private ExcelReader() {
    }

    /**
     * 同步读取 异步读取.
     *
     * @param inputStream the input stream
     * @param flag        the flag
     * @param config      the config
     * @return the map
     * @throws IOException the io exception
     */
    public static Map<String, List<Map<String, String>>> syncReader(InputStream inputStream, boolean flag, ExcelConfig config) throws IOException {

        Workbook wb = flag ? new HSSFWorkbook(inputStream) : new XSSFWorkbook(inputStream);
        Map<String, List<Map<String, String>>> data = new HashMap<>();
        Iterator<Sheet> sheetIterator = wb.sheetIterator();
//        遍历所有 sheet
        while (sheetIterator.hasNext()) {
            Sheet sheet = sheetIterator.next();
            sheetReader(sheet, data, config);
        }
        return data;
    }

    /**
     * 多线程读取数据.
     * excel的每个sheet 会被独立的线程来解析过滤 处理
     *
     * @param inputStream the input stream
     * @param flag        the flag
     * @param config      the config
     * @return the map
     * @throws ExecutionException   the execution exception
     * @throws InterruptedException the interrupted exception
     * @throws IOException          the io exception
     */
    public static Map<String, List<Map<String, String>>> asyncReader(InputStream inputStream, boolean flag, final ExcelConfig config) throws ExecutionException, InterruptedException, IOException {
        Workbook workbook = flag ? new HSSFWorkbook(inputStream) : new XSSFWorkbook(inputStream);
        Map<String, List<Map<String, String>>> data = new HashMap<>();
        Iterator<Sheet> sheetIterator = workbook.sheetIterator();


        int sheetsCount = workbook.getNumberOfSheets();
        ExecutorService executorService = Executors.newFixedThreadPool(sheetsCount);
        List<Future<Map<String, List<Map<String, String>>>>> futures = new ArrayList<>(sheetsCount);

        while (sheetIterator.hasNext()) {
            final Sheet sheet = sheetIterator.next();
            Callable<Map<String, List<Map<String, String>>>> task = () -> {
                Map<String, List<Map<String, String>>> sheetData = new HashMap<>();
                sheetReader(sheet, sheetData, config);
                return sheetData;
            };
            Future<Map<String, List<Map<String, String>>>> future = executorService.submit(task);
            futures.add(future);
        }

        for (Future<Map<String, List<Map<String, String>>>> future : futures) {
            data.putAll(future.get());
        }
        executorService.shutdown();
        return data;

    }

    /**
     * sheet解析
     *
     * @param sheet
     * @param sheetData sheet解析数据 包括合法   不合法
     * @param config
     */
    private static void sheetReader(Sheet sheet, Map<String, List<Map<String, String>>> sheetData, ExcelConfig config) {
        String sheetName = sheet.getSheetName();

        Iterator<Row> rowIterator = sheet.iterator();
        List<Map<String, String>> list = new ArrayList<>();
        List<Map<String, String>> errorList = new ArrayList<>();
//           外层循环标识 用于跳过
//            Outer:
//            遍历row
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            int rowNum = row.getRowNum();
//                实际数据行开始读取  校验并 写入
            if (rowNum >= config.getRowNum()) {
                Iterator<Cell> cellIterator = row.cellIterator();
                int index = 0;
                Map<String, String> map = new HashMap<>();
//                    遍历cell
                boolean router = true;
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    String val = cellReader(cell);
                    String key = config.getColumnNames()[index];
                    CellRule rule = config.getRules().get(key);
                    boolean checked = Validator.checkVal(val, rule);

                    if (!checked) {
                        router = false;
                    }
                    map.put(key, val);

                    index++;
                }
                if (router) {
                    list.add(map);
                } else {
                    errorList.add(map);
                }
            }
        }
//            有效 集合 错误集合分类 被传递出去
        if (list.size() > 0) {
            sheetData.put(sheetName, list);
        }
        if (errorList.size() > 0) {
            String key = "error" + sheetName;
            sheetData.put(key, errorList);
        }
    }


    /**
     * 解析 cell 规则
     *
     * @param cell cell
     * @return 去除空格 cell String
     */
    private static String cellReader(Cell cell) {
        String cellValue;
        if (cell != null) {
            switch (cell.getCellType()) { // 判断excel单元格内容的格式，并对其进行转换，以便插入数据库
                case 0:
                    cellValue = String.valueOf((int) cell.getNumericCellValue());
                    break;
                case 1:
                    cellValue = String.valueOf(cell.getStringCellValue());
                    break;
                case 2:
                    cellValue = String.valueOf(cell.getDateCellValue());
                    break;
                case 4:
                    cellValue = String.valueOf(cell.getBooleanCellValue());
                    break;
                case 5:
                    cellValue = String.valueOf(cell.getErrorCellValue());
                    break;
                default:
                    cellValue = "";
                    break;
            }
        } else {
            cellValue = "";
        }
        return cellValue.trim();
    }
}