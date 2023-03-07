package com.dengzh.wyy.hosp.service.impl;

import com.dengzh.wyy.hosp.repository.ScheduleRepository;
import com.dengzh.wyy.hosp.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;
}
