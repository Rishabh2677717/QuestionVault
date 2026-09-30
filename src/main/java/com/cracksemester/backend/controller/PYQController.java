package com.cracksemester.backend.controller;

import com.cracksemester.backend.dto.PYQRequest;
import com.cracksemester.backend.entity.PYQ;
import com.cracksemester.backend.service.PYQService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pyqs")
public class PYQController {

    private final PYQService pyqService;

    public PYQController(PYQService pyqService) {
        this.pyqService = pyqService;
    }

    @GetMapping
    public ResponseEntity<List<PYQ>> getAllPYQs() {
        return ResponseEntity.ok(pyqService.getAllPYQs());
    }

    @GetMapping("/subject/{subjectId}")
    public ResponseEntity<List<PYQ>> getPYQsBySubject(@PathVariable String subjectId) {
        return ResponseEntity.ok(pyqService.getPYQsBySubject(subjectId));
    }

    @PostMapping
    public ResponseEntity<PYQ> savePYQ(@RequestBody PYQRequest request) {
        return ResponseEntity.ok(pyqService.save(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PYQ> updatePYQ(@PathVariable String id, @RequestBody PYQRequest request) {
        return ResponseEntity.ok(pyqService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePYQ(@PathVariable String id) {
        pyqService.deletePYQ(id);
        return ResponseEntity.ok("Deleted successfully");
    }
}
