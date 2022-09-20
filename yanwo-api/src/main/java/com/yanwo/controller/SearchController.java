package com.yanwo.controller;

import com.yanwo.service.SearchService;
import com.yanwo.utils.JsonUtils;
import com.yanwo.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Created by Administrator on 2018/5/19.
 */
@RestController
public class SearchController {

    @Autowired
    SearchService searchService;


    @RequestMapping(value="/search", method = RequestMethod.POST)
    public R search(Model model,
                    @RequestParam(defaultValue = "") String keyword,
                    @RequestParam(defaultValue = "") String priceSection,
                    @RequestParam(defaultValue = "") String catId,
                    @RequestParam(defaultValue = "") String gradeSort,
                    @RequestParam(defaultValue = "") String sort,
                    @RequestParam(defaultValue = "") String itemType,
                    @RequestParam(defaultValue = "1") int page,
                    @RequestParam(defaultValue = "50") int pageSize
    ){
        String r = searchService.findSearchResult(keyword,priceSection,catId, gradeSort,sort,itemType, page, pageSize);
        Map result= JsonUtils.jsonToMap(r);
        return R.ok(result);
    }

}
