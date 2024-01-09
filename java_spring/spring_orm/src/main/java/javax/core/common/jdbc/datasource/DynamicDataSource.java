package javax.core.common.jdbc.datasource;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/** 
 * 动态数据源 
 *  
 * @author Tom 
 *  
 */  
public class DynamicDataSource extends AbstractRoutingDataSource {  


    //entry的目的，主要是用来给每个数据源打个标记
	private DynamicDataSourceEntry dataSourceEntry;  
    
    @Override  
    protected Object determineCurrentLookupKey() {
        return this.dataSourceEntry.get();  
    }  
  
    public void setDataSourceEntry(DynamicDataSourceEntry dataSourceEntry) {  
        this.dataSourceEntry = dataSourceEntry;
    }
    
    public DynamicDataSourceEntry getDataSourceEntry(){
    		return this.dataSourceEntry;
    }
    
}
