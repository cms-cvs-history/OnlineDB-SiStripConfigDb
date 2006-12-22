// Last commit: $Id: $
// Latest tag:  $Name:  $
// Location:    $Source: $

#ifndef OnlineDB_SiStripConfigDb_test_DatabaseService_H
#define OnlineDB_SiStripConfigDb_test_DatabaseService_H

#include "FWCore/Framework/interface/EDAnalyzer.h"
#include "FWCore/Framework/interface/EventSetup.h"
#include "FWCore/Framework/interface/Event.h"
#include "FWCore/ParameterSet/interface/ParameterSet.h"

class SiStripConfigDb;

/**
   @class test_DatabaseService 
   @author R.Bainbridge
   @brief Simple class that tests SiStripConfigDb service
*/
class test_DatabaseService : public edm::EDAnalyzer {

 public:
  
  test_DatabaseService( const edm::ParameterSet& );
  ~test_DatabaseService();
  
  void beginJob( edm::EventSetup const& );
  void analyze( const edm::Event&, const edm::EventSetup& ) {;}
  void endJob() {;}

 private:

  SiStripConfigDb* db_;
  
};

#endif // OnlineDB_SiStripConfigDb_test_DatabaseService_H

