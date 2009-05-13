package com.omniti.reconnoiter;

import java.lang.System;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import com.omniti.reconnoiter.AMQListener;
import com.omniti.reconnoiter.event.NoitEvent;
import com.espertech.esper.client.*;
import com.espertech.esper.client.soda.*;

import org.apache.log4j.BasicConfigurator;

class IEPEngine {
  static public void main(String[] args) {
    BasicConfigurator.configure();

    Configuration config = new Configuration();
    EPServiceProvider epService = EPServiceProviderManager.getDefaultProvider(config);
    config.addEventTypeAutoName("com.omniti.reconnoiter.event");
    NoitEvent.registerTypes(epService);

    epService.getEPAdministrator().createEPL("create window CheckDetails.std:unique(uuid).win:keepall() as NoitCheck");
    epService.getEPAdministrator().createEPL("insert into CheckDetails select * from NoitCheck");

    AMQListener l = new AMQListener(epService);

    Thread listener = new Thread(l);
    listener.start();

    BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
    while(true) {
      try {
        if(stdin.readLine() == null) throw new Exception("all done");
      } catch(Exception e) {
        System.exit(-1);
      }
    }
  }
}