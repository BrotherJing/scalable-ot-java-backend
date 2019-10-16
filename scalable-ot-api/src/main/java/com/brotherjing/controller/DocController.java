package com.brotherjing.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.brotherjing.core.service.DocService;
import com.brotherjing.proto.TextProto;

@RestController
@RequestMapping(path = "/doc")
public class DocController {

    @Autowired
    private DocService docService;

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    TextProto.Snapshot create() {
        return docService.create();
    }

    @GetMapping(value = "/{docId}/fetch")
    TextProto.Snapshot fetch(@PathVariable String docId) {
        return docService.get(docId);
    }
}
