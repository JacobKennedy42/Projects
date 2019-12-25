FROM ubuntu:16.04
  
# install requirements
RUN apt-get update && \
    apt-get install -y \
        build-essential \
        git \
        make \
        g++ \
        vim \
        wget \
        expect \
        qemu \
        g++-multilib

#RUN mkdir /work
#COPY * /work/

WORKDIR /work

# entry point
CMD ["make", "-k", "test"]
