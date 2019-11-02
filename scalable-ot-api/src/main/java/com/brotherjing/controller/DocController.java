package com.brotherjing.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.brotherjing.core.service.DocService;
import com.brotherjing.producer.OpSender;
import com.brotherjing.proto.BaseProto;

@RestController
@RequestMapping(path = "/doc")
public class DocController {

    @Autowired
    private DocService docService;

    @Autowired
    private OpSender opSender;

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    BaseProto.Snapshot create() {
        return docService.create();
    }

    @GetMapping(value = "/{docId}/fetch")
    BaseProto.Snapshot fetch(@PathVariable String docId,
            @RequestParam(value = "version", required = false) Integer version) {
        if (version != null) {
            return docService.getSnapshotAt(docId, version);
        }
        return docService.get(docId);
    }

    @GetMapping(value = "/{docId}/ops")
    BaseProto.Commands getOps(@PathVariable String docId, @RequestParam("from") int fromVersion) {
        List<BaseProto.Command> commands = docService.getOpsSince(docId, fromVersion);
        return BaseProto.Commands.newBuilder()
                                 .addAllCommands(commands)
                                 .build();
    }

    @PostMapping(value = "/{docId}/save", consumes = "application/x-protobuf")
    void save(@PathVariable String docId, @RequestBody BaseProto.Command command) {
        opSender.send(docId, command);
    }
}
