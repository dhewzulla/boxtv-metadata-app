muleappname=boxtv-metadata-app
version=1.0.1.zip
cd ~/box-config
git add . --all
git commit -m "developing"
git push origin develop
cd -
ssh davran@userver 'cd ~/box-config && git pull origin'
echo "...bcs3uploaded"
cd ~/bcs3uploader
git add . --all
git commit -m "developing"

git push origin master
cd -
echo "deploying bcs3uploaded..."
ssh davran@userver 'cd ~/bcs3uploader && git pull origin'

echo "***deploying version $muleapp"

ssh davran@userver 'rm ~/bdocker/bmule/opt/mule/apps/$muleappname*.txt' 

scp target/$muleappname*-SNAPSHOT.zip davran@userver:bdocker/bmule/opt/mule/apps/
