// Last commit: $Id: $
// Latest tag:  $Name:  $
// Location:    $Source: $

#include "OnlineDB/SiStripConfigDb/test/stubs/test_DatabaseService.h"
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
test_DatabaseService::test_DatabaseService( const edm::ParameterSet& pset ) 
  : db_(0)
{
  LogDebug(mlCabling_)
    << "[test_DatabaseService::" << __func__ << "]"
    << " Constructing object...";
}

// -----------------------------------------------------------------------------
// 
test_DatabaseService::~test_DatabaseService() {
  LogDebug(mlCabling_)
    << "[test_DatabaseService::" << __func__ << "]"
    << " Destructing object...";
}

// -----------------------------------------------------------------------------
// 
void test_DatabaseService::beginJob( const edm::EventSetup& setup ) {
  
  db_ = edm::Service<SiStripConfigDb>().operator->();
  edm::LogVerbatim(mlCabling_)
    << "[test_DatabaseService::" << __func__ << "]"
    << " Ptr1 to SiStripConfigDb: " << db_;

  if ( db_ ) { 
    edm::LogVerbatim(mlCabling_)
      << "[test_DatabaseService::" << __func__ << "]"
      << " Ptr1 to DeviceFactory: " << db_->deviceFactory();
  }

  db_ = edm::Service<SiStripConfigDb>().operator->();
  edm::LogVerbatim(mlCabling_)
    << "[test_DatabaseService::" << __func__ << "]"
    << " Ptr2 to SiStripConfigDb: " << db_;

  if ( db_ ) { 
    edm::LogVerbatim(mlCabling_)
      << "[test_DatabaseService::" << __func__ << "]"
      << " Ptr2 to DeviceFactory: " << db_->deviceFactory();
  }
  
}

