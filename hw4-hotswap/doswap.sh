#/bin/bash

function killitif {
    docker ps -a  > /tmp/yy_xx$$
    if grep --quiet web1 /tmp/yy_xx$$
     then
     echo "killing web1"
     docker rm -f `docker ps -a | grep web1  | sed -e 's: .*$::'`
     docker run --name web2 --net ecs189_default -dP $1
     sleep 10 && docker exec ecs189_proxy_1 /bin/bash /bin/swap2.sh
     echo "redirecting to the service" 
	 echo "...nginx restarted, should be ready to go!" 
	else
	 echo "killing web2"
	 docker rm -f `docker ps -a | grep web2  | sed -e 's: .*$::'`
	 docker run --name web1 --net ecs189_default -dP $1
	 sleep 10 && docker exec ecs189_proxy_1 /bin/bash /bin/swap1.sh
	 echo "redirecting to the service" 
	 echo "...nginx restarted, should be ready to go!" 
   fi
}


# Remove any existing containers, so we don't have failure
# on the run command because of existing named containers. 

killitif $1

