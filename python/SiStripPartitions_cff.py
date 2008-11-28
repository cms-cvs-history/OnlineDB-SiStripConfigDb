import FWCore.ParameterSet.Config as cms

SiStripPartitions = cms.PSet(

    PrimaryPartition = cms.untracked.PSet(
    
        PartitionName = cms.untracked.string(''),

	# Forces versions to be taken from "current state". Overrides ALL settings below.
        ForceCurrentState = cms.untracked.bool(False),

	# 1) A non-zero value means "retrieve versions from run table".
	# 2) A null value means "use versions listed below". {0,0} means "current state".
        RunNumber = cms.untracked.uint32(0),

	# Only "active" with a non-zero run number.
	# 1) A non-zero version means "override version retrieved from run table".
	# 2) A null value {0,0} means "do not override versions retrieved from run table".
        ForceVersions = cms.untracked.bool(False),

	# Syntax is {major,minor}. 
        CablingVersion   = cms.untracked.vuint32(0,0),
        FecVersion       = cms.untracked.vuint32(0,0),
        FedVersion       = cms.untracked.vuint32(0,0),
        DcuDetIdsVersion = cms.untracked.vuint32(0,0),
        DcuPsuMapVersion = cms.untracked.vuint32(0,0),

        # A non-zero number means "retrieve a complete calibration set".
        # A null value means "use analysis versions below". {0,0} means "current state".
        GlobalAnalysisVersion = cms.untracked.uint32(0),

	# Syntax is {major,minor}. 
        FastCablingVersion = cms.untracked.vuint32(0,0),
        ApvTimingVersion   = cms.untracked.vuint32(0,0),
        OptoScanVersion    = cms.untracked.vuint32(0,0),
        VpspScanVersion    = cms.untracked.vuint32(0,0),
        ApvCalibVersion    = cms.untracked.vuint32(0,0),
        PedestalsVersion   = cms.untracked.vuint32(0,0),
        ApvLatencyVersion  = cms.untracked.vuint32(0,0),
        FineDelayVersion   = cms.untracked.vuint32(0,0),

        # XML input files
        InputModuleXml  = cms.untracked.string('/afs/cern.ch/cms/cmt/onlinedev/data/examples/module.xml'),
        InputDcuInfoXml = cms.untracked.string('/afs/cern.ch/cms/cmt/onlinedev/data/examples/dcuinfo.xml'),
        InputFedXml     = cms.untracked.vstring(''),
        InputFecXml     = cms.untracked.vstring('')

    )

    # Additional partitions here...

)

