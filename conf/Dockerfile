ARG VERSION
FROM nginx:$VERSION

ARG CONF_FOLDER=myappl
RUN mkdir -p /opt/myappl/conf
COPY ./${CONF_FOLDER} /opt/myappl/conf/

ENTRYPOINT sleep 120 && echo "OK" >> /STARTED && tail -f /STARTED
HEALTHCHECK --interval=10s --timeout=1h \
  CMD test -f "/STARTED" || exit 1