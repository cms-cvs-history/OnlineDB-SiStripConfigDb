// Last commit: $Id: test_DatabaseService.cc,v 1.1 2007/05/11 12:05:08 bainbrid Exp $

#include "OnlineDB/SiStripConfigDb/test/plugins/testDatabaseService.h"
#include "FWCore/ServiceRegistry/interface/Service.h"
#include "FWCore/MessageLogger/interface/MessageLogger.h"
#include "DataFormats/SiStripCommon/interface/SiStripConstants.h"
#include "OnlineDB/SiStripConfigDb/interface/SiStripConfigDb.h"
#include <iostream>
#include <sstream>
#include <string>

using namespace std;
using namespace sistrip;

// -----------------------------------------------------------------------------
// 
testDatabaseService::testDatabaseService( const edm::ParameterSet& pset ) 
  : db_(0)
{
  LogDebug(mlCabling_)
    << "[testDatabaseService::" << __func__ << "]"
    << " Constructing object...";
}

// -----------------------------------------------------------------------------
// 
testDatabaseService::~testDatabaseService() {
  LogDebug(mlCabling_)
    << "[testDatabaseService::" << __func__ << "]"
    << " Destructing object...";
}

// -----------------------------------------------------------------------------
// 
void testDatabaseService::beginJob( const edm::EventSetup& setup ) {
  
  db_ = edm::Service<SiStripConfigDb>().operator->();
  edm::LogVerbatim(mlCabling_)
    << "[testDatabaseService::" << __func__ << "]"
    << " Ptr1 to SiStripConfigDb: " << db_;

  if ( db_ ) { 
    edm::LogVerbatim(mlCabling_)
      << "[testDatabaseService::" << __func__ << "]"
      << " Ptr1 to DeviceFactory: " << db_->deviceFactory();
  }

  db_ = edm::Service<SiStripConfigDb>().operator->();
  edm::LogVerbatim(mlCabling_)
    << "[testDatabaseService::" << __func__ << "]"
    << " Ptr2 to SiStripConfigDb: " << db_;

  if ( db_ ) { 
    edm::LogVerbatim(mlCabling_)
      << "[testDatabaseService::" << __func__ << "]"
      << " Ptr2 to DeviceFactory: " << db_->deviceFactory();
  }
  
}

