cp scripts/respec-template.html index.html
index=`cat index.html`
top=`cat specifications/tx.dataspace.topology.md`
topph="THIS IS THE PLACEHOLDER FOR THE DATASPACE TOPOLOGY"
base=`cat specifications/identity.protocol.base.md`
baseph="THIS IS THE PLACEHOLDER FOR THE BASE PROTOCOL"
pres=`cat specifications/verifiable.presentation.protocol.md`
presph="THIS IS THE PLACEHOLDER FOR THE PRESENTATION PROTOCOL"
iss=`cat specifications/credential.issuance.protocol.md`
issph="THIS IS THE PLACEHOLDER FOR THE ISSUANCE PROTOCOL"
index="${index/$topph/"$top"}"
index="${index/$baseph/"$base"}"
index="${index/$presph/"$pres"}"
index="${index/$issph/"$iss"}"
echo "$index" > index.html