package com.example.hello.service.impl;

import com.example.hello.dto.FaultTypeTreeNode;
import com.example.hello.entity.FaultType;
import com.example.hello.mapper.FaultTypeMapper;
import com.example.hello.service.FaultTypeService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FaultTypeServiceImpl implements FaultTypeService {

    private final FaultTypeMapper faultTypeMapper;

    public FaultTypeServiceImpl(FaultTypeMapper faultTypeMapper) {
        this.faultTypeMapper = faultTypeMapper;
    }

    @Override
    public List<FaultTypeTreeNode> getTree() {
        List<FaultType> all = faultTypeMapper.selectAllEnabled();
        Map<Long, List<FaultType>> byParent = all.stream()
                .collect(Collectors.groupingBy(f -> f.getParentId() == null ? 0L : f.getParentId()));

        List<FaultType> roots = byParent.getOrDefault(0L, new ArrayList<>());
        return roots.stream()
                .sorted((a, b) -> Integer.compare(a.getSortOrder() != null ? a.getSortOrder() : 0,
                        b.getSortOrder() != null ? b.getSortOrder() : 0))
                .map(r -> buildNode(r, byParent))
                .collect(Collectors.toList());
    }

    private FaultTypeTreeNode buildNode(FaultType ft, Map<Long, List<FaultType>> byParent) {
        FaultTypeTreeNode node = new FaultTypeTreeNode();
        node.setId(ft.getId());
        node.setTypeName(ft.getTypeName());

        List<FaultType> children = byParent.getOrDefault(ft.getId(), new ArrayList<>());
        if (!children.isEmpty()) {
            node.setChildren(children.stream()
                    .sorted((a, b) -> Integer.compare(a.getSortOrder() != null ? a.getSortOrder() : 0,
                            b.getSortOrder() != null ? b.getSortOrder() : 0))
                    .map(c -> buildNode(c, byParent))
                    .collect(Collectors.toList()));
        }
        return node;
    }
}
