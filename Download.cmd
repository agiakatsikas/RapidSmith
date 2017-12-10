setMode -bs
setCable -port auto
setCable -target "digilent_plugin DEVICE=SN: FREQUENCY=-1"
Identify -inferir
identifyMPM
assignFile -p 1 -file "E:/FCCM18/Test/aes_1/fie/fie.sdk/fie_hw_platform_0/faultInjection/aes_1/download.bit"
Program -p 1
exit
