muleapp=boxtv-metadata-app-1.0.0-SNAPSHOT.zip
mvn install
cd ~/box-config
git add . --all
git commit -m "developing"
git push origin
cd -
ssh davran@dev.boxnetwork.co.uk 'cd ~/box-config && git pull origin'
scp target/$muleapp davran@dev.boxnetwork.co.uk:bdocker/bmule/opt/mule/apps/
