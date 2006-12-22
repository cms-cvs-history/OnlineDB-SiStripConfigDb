// Last commit: $Id: $
// Latest tag:  $Name:  $
// Location:    $Source: $

#include "FWCore/Framework/interface/MakerMacros.h"
#include "PluginManager/ModuleDef.h"
DEFINE_SEAL_MODULE();

#include "OnlineDB/SiStripConfigDb/test/stubs/test_DatabaseService.h"
DEFINE_ANOTHER_FWK_MODULE(test_DatabaseService);

#include "OnlineDB/SiStripConfigDb/test/stubs/PopulateConfigDb.h"
DEFINE_ANOTHER_FWK_MODULE(PopulateConfigDb);
