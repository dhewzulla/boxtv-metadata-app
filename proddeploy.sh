muleapp=boxtv-metadata-app-1.0.1.zip
cd ~/box-config
git add . --all
git commit -m "developing"
git push origin
cd -

ssh ec2-user@boxnetwork.co.uk 'cd ~/box-config && git pull origin'

ssh ec2-user@boxnetwork.co.uk 'cd ~/bcs3uploader && git pull origin'


scp target/$muleapp ec2-user@boxnetwork.co.uk:bdocker/bmule/opt/mule/apps/
