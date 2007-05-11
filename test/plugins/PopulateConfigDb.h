// Last commit: $Id: PopulateConfigDb.h,v 1.1 2006/12/22 12:18:38 bainbrid Exp $
// Latest tag:  $Name: HEAD $
// Location:    $Source: /cvs_server/repositories/CMSSW/CMSSW/OnlineDB/SiStripConfigDb/test/stubs/PopulateConfigDb.h,v $

#ifndef OnlineDB_SiStripESSources_test_PopulateConfigDb_H
#define OnlineDB_SiStripESSources_test_PopulateConfigDb_H

#include "FWCore/Framework/interface/EDAnalyzer.h"
#include "FWCore/Framework/interface/EventSetup.h"
#include "FWCore/Framework/interface/Event.h"
#include "FWCore/ParameterSet/interface/ParameterSet.h"
#include "OnlineDB/SiStripConfigDb/interface/SiStripConfigDb.h"
#include "boost/cstdint.hpp"
#include <string>
#include <vector>
#include <map>

class SiStripFecCabling;

class PopulateConfigDb : public edm::EDAnalyzer {
  
 public:

  /** vector of pairs of DetId and number of APVs. */ 
  typedef std::vector< std::pair<uint32_t,uint16_t> > TkPartition;
  typedef std::vector< std::pair<std::string,TkPartition> > TkPartitions;
  
  PopulateConfigDb( const edm::ParameterSet& ); 
  ~PopulateConfigDb(); 
  
  virtual void beginJob( const edm::EventSetup& );
  virtual void analyze( const edm::Event&, const edm::EventSetup& ) {;}
  
 private: // ----- methods -----
  
  void retrieveDetIds( const edm::EventSetup&,
		       const uint32_t& max_number_of_dets,
		       TkPartitions& );
  
  void createFecCabling( const uint16_t& partition_number,
			 const TkPartitions& partitions,
			 SiStripFecCabling& fec_cabling,
			 SiStripConfigDb::DcuDetIdMap& dcu_detid_map );
  
 private: // ----- data members -----
  
  SiStripConfigDb* db_;
  
  uint32_t maxNumberOfDets_;

  uint16_t chansPerFed_;

  bool upload_;
  
};

#endif // OnlineDB_SiStripESSources_test_PopulateConfigDb_H

