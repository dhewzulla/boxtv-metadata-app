scriptbasedir=$(dirname "$0")
source $scriptbasedir/../properties.sh
curl -d "{episodeid:1}" -X POST $mulebaseurl/bc/ingest
