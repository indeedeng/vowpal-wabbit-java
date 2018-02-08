FROM centos:6

RUN yum update -q -y
RUN yum install -y epel-release
RUN yum update -q -y
RUN yum install -y yum install -y make pkg-config git file wget centos-release-SCL scl-utils-build which zlib-devel java-1.7.0-openjdk-devel perl redhat-lsb-core environment-modules


# Install c++ from people.centos.org
RUN cd /etc/yum.repos.d
RUN wget http://people.centos.org/tru/devtools-2/devtools-2.repo -O /etc/yum.repos.d/devtools-2.repo
RUN yum install -y devtoolset-2-gcc devtoolset-2-binutils
RUN yum install -y devtoolset-2-gcc-c++ devtoolset-2-gcc-gfortran
RUN scl enable devtoolset-2 bash
RUN source /opt/rh/devtoolset-2/enable

ENV PATH=/opt/rh/devtoolset-2/root/usr/bin/:$PATH
ENV JAVA_HOME=/usr/lib/jvm/java-1.7.0-openjdk.x86_64

# Install autotools from sources
RUN wget http://ftp.gnu.org/gnu/m4/m4-1.4.18.tar.gz
RUN tar xvzf m4-1.4.18.tar.gz
RUN cd /m4-1.4.18 && ./configure && make && make install

RUN wget http://ftp.gnu.org/gnu/autoconf/autoconf-2.69.tar.gz
RUN tar xvzf autoconf-2.69.tar.gz
RUN cd /autoconf-2.69 && ./configure && make && make install

RUN wget http://ftp.gnu.org/gnu/automake/automake-1.15.tar.gz
RUN tar xvzf automake-1.15.tar.gz
RUN cd /automake-1.15 && ./configure && make && make install

RUN wget http://ftpmirror.gnu.org/libtool/libtool-2.4.6.tar.gz
RUN tar xvzf libtool-2.4.6.tar.gz
RUN cd /libtool-2.4.6 && ./configure && make && make install
