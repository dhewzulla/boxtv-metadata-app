muleappname=boxtv-metadata-app



cd ~/box-config
git add . --all
git commit -m "developing"
git push origin
cd -

ssh ec2-user@boxnetwork.co.uk 'cd ~/box-config && git pull origin'

ssh ec2-user@boxnetwork.co.uk 'cd ~/bcs3uploader && git pull origin'

echo "deploying version $muleapp"

rm target/*-SNAPSHOT.zip

ssh ec2-user@boxnetwork.co.uk 'rm ~/bdocker/bmule/opt/mule/apps/$muleappname*.txt'
scp target/$muleappname*.zip ec2-user@boxnetwork.co.uk:bdocker/bmule/opt/mule/apps/
