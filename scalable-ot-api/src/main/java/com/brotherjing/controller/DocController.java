package com.brotherjing.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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

    @GetMapping(value = "/{docId}/ops")
    TextProto.Commands getOps(@PathVariable String docId, @RequestParam("from") int fromVersion) {
        List<TextProto.Command> commands = docService.getOpsSince(docId, fromVersion);
        return TextProto.Commands.newBuilder()
                                 .addAllCommands(commands)
                                 .build();
    }
}
