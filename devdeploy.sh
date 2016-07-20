muleapp=boxtv-metadata-app-1.0.0-SNAPSHOT.zip

cd ~/box-config
git add . --all
git commit -m "developing"
git push origin master
cd -
ssh davran@www.davran.co.uk 'cd ~/box-config && git pull origin'

cd ~/bcs3uploader
git add . --all
git commit -m "developing"
git push origin master
cd -
ssh davran@www.davran.co.uk 'cd ~/bcs3uploader && git pull origin'


scp target/$muleapp davran@www.davran.co.uk:bdocker/bmule/opt/mule/apps/
