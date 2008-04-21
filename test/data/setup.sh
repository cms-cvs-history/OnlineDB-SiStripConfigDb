eval `scramv1 runtime -sh`
scramv1 tool remove tkonlinesw
scramv1 setup tkonlinesw 0.3-cms file:$CMSSW_BASE/src/OnlineDB/SiStripConfigDb/test/data/tkonlinesw
eval `scramv1 runtime -sh`
cd $CMSSW_BASE/src/OnlineDB/SiStripConfigDb/interface/
replace "//#define USING_NEW_DATABASE_MODEL" "#define USING_NEW_DATABASE_MODEL" -- SiStripConfigDb.h
cd -
