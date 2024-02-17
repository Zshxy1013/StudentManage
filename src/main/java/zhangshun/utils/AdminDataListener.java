package zhangshun.utils;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.util.ListUtils;
import zhangshun.domain.AdminDetails;
import zhangshun.service.AdminManageService;

import java.util.List;

// 有个很重要的点 DemoDataListener 不能被spring管理，要每次读取excel都要new,然后里面用到spring可以构造方法传进去
public class AdminDataListener implements ReadListener<AdminDetails> {

    private static final int BATCH_COUNT = 100;
    private final AdminManageService adminManageService;
    private List<AdminDetails> cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);

    public AdminDataListener(AdminManageService adminManageService) {
        this.adminManageService = adminManageService;
    }


    @Override
    public void invoke(AdminDetails data, AnalysisContext context) {
        cachedDataList.add(data);
        // 达到BATCH_COUNT了，需要去存储一次数据库，防止数据几万条数据在内存，容易OOM
        if (cachedDataList.size() >= BATCH_COUNT) {
            saveData();
            cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
        }
    }


    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // 这里也要保存数据，确保最后遗留的数据也存储到数据库
        saveData();
    }

    private void saveData() {
        adminManageService.ExcelAddAdmin(cachedDataList);
    }
}

