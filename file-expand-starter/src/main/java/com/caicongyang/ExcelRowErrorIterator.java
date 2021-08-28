package com.caicongyang;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

public class ExcelRowErrorIterator implements Iterable<ExcelRowError> {
    private List<ExcelCellError> excelErrors = new ArrayList<ExcelCellError>();
    
    ExcelRowErrorIterator(List<ExcelCellError> excelErrors) {
        this.excelErrors = excelErrors;
    }

    @Override
    public Iterator<ExcelRowError> iterator() {
        return new Iterator<ExcelRowError>() {
            private int rowIdx = -1;
            private int remainSize = excelErrors.size();
            private List<Integer> tmpRemovedIndexes = new ArrayList<>();
            private List<Integer> removedIndexes = new ArrayList<>();
            
            @Override
            public boolean hasNext() {
                return ! excelErrors.isEmpty() && remainSize > 0;
            }
            
            @Override
            public ExcelRowError next() {
                List<String> errors = new ArrayList<String>();
                
                int index = 0;
                Row row = null;
                
                while (index < excelErrors.size()) {
                	if (! removedIndexes.contains(index)) {
                        ExcelCellError error = excelErrors.get(index);
                        Cell cell = error.getCell();
                        int _rowIdx = cell.getRowIndex();
                        if (rowIdx == -1) {
                        	rowIdx = _rowIdx;
                        	row = cell.getRow();
                        }
                        
                        if (_rowIdx == rowIdx) {
                            errors.add(error.getError());
                            removedIndexes.add(index);
                            tmpRemovedIndexes.add(index);
                            remainSize --;
                        }
        
                        if (index == excelErrors.size() - 1) {
                            rowIdx = -1;
                            return new ExcelRowError(row, errors.toArray(new String[errors.size()]));
                        }
                	}
                    
                    index ++;
                }
                return null;
            }

			@Override
			public void remove() {
				for (int index : tmpRemovedIndexes) {
					excelErrors.remove(index);
				}
                tmpRemovedIndexes.clear();
			}
        };
    }

}
