scriptbasedir=$(dirname "$0")
source $scriptbasedir/../properties.sh
curl -i $mulebaseurl/episodes/$1
