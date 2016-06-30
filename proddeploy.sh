muleapp=boxtv-metadata-app-1.0.0-SNAPSHOT.zip
cd ~/box-config
git add . --all
git commit -m "developing"
git push origin
cd -
ssh ec2-user@boxnetwork.co.uk 'cd ~/box-config && git pull origin'
scp target/$muleapp ec2-user@boxnetwork.co.uk:bdocker/bmule/opt/mule/apps/
