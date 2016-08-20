muleappname=boxtv-metadata-app
version=1.0.1.zip
cd ~/box-config
git add . --all
git commit -m "developing"
git push origin develop
cd -
ssh davran@www.davran.co.uk 'cd ~/box-config && git pull origin'
echo "...bcs3uploaded"
cd ~/bcs3uploader
git add . --all
git commit -m "developing"

#git push origin master
cd -
echo "deploying bcs3uploaded..."
ssh davran@www.davran.co.uk 'cd ~/bcs3uploader && git pull origin'

echo "***deploying version $muleapp"

ssh davran@www.davran.co.uk 'rm ~/bdocker/bmule/opt/mule/apps/$muleappname*.txt' 

scp target/$muleappname*-SNAPSHOT.zip davran@www.davran.co.uk:bdocker/bmule/opt/mule/apps/
