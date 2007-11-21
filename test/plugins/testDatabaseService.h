// Last commit: $Id: test_DatabaseService.h,v 1.1 2007/05/11 12:05:08 bainbrid Exp $
// Latest tag:  $Name:  $
// Location:    $Source: /cvs_server/repositories/CMSSW/CMSSW/OnlineDB/SiStripConfigDb/test/plugins/test_DatabaseService.h,v $

#ifndef OnlineDB_SiStripConfigDb_testDatabaseService_H
#define OnlineDB_SiStripConfigDb_testDatabaseService_H

#include "FWCore/Framework/interface/EDAnalyzer.h"
#include "FWCore/Framework/interface/EventSetup.h"
#include "FWCore/Framework/interface/Event.h"
#include "FWCore/ParameterSet/interface/ParameterSet.h"

class SiStripConfigDb;

/**
   @class testDatabaseService 
   @author R.Bainbridge
   @brief Simple class that tests SiStripConfigDb service
*/
class testDatabaseService : public edm::EDAnalyzer {

 public:
  
  testDatabaseService( const edm::ParameterSet& );
  ~testDatabaseService();
  
  void beginJob( edm::EventSetup const& );
  void analyze( const edm::Event&, const edm::EventSetup& ) {;}
  void endJob() {;}

 private:

  SiStripConfigDb* db_;
  
};

#endif // OnlineDB_SiStripConfigDb_testDatabaseService_H

