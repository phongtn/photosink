FROM amazoncorretto:17-alpine
ENV TZ=Asia/Ho_Chi_Minh

LABEL author="phongtn.group1@gmail.com"

COPY ./build/distributions/*.tar /dis.tar
#ADD build/distributions/*.tar /app
WORKDIR /app
RUN ulimit -c unlimited
RUN tar --version
RUN tar xf /dis.tar -C /app --strip-components=1
RUN ls -la

CMD /app/bin/GooglePhotoBackup
EXPOSE 8080