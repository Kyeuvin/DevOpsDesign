import React, { useEffect, useState } from 'react';
import { Client } from '@stomp/stompjs';
import { Card, List, Tag } from 'antd';
import { BuildStatus } from './types';

interface Props {
  jobName: string;
}

const BuildMonitor: React.FC<Props> = ({ jobName }) => {
  const [status, setStatus] = useState<BuildStatus | null>(null);
  const [logs, setLogs] = useState<string>('');
  const [client, setClient] = useState<Client | null>(null);

  useEffect(() => {
    const stompClient = new Client({
      brokerURL: 'ws://localhost:8080/ws',
      debug: function (str) {
        console.log(str);
      },
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
    });

    stompClient.onConnect = () => {
      // 订阅构建状态
      stompClient.subscribe(`/topic/builds/${jobName}`, message => {
        const update = JSON.parse(message.body);
        setStatus(update);
      });

      // 订阅构建日志
      stompClient.subscribe(`/topic/logs/${jobName}/${status?.buildNumber}`, message => {
        setLogs(prevLogs => prevLogs + message.body);
      });
    };

    stompClient.activate();
    setClient(stompClient);

    return () => {
      if (client) {
        client.deactivate();
      }
    };
  }, [jobName, status?.buildNumber]);

  return (
    <Card title={`Build Monitor - ${jobName}`}>
      <div>
        <h3>Status: <Tag color={getStatusColor(status?.status)}>{status?.status}</Tag></h3>
        <h3>Build #{status?.buildNumber}</h3>
      </div>
      <div className="logs">
        <pre>{logs}</pre>
      </div>
    </Card>
  );
};

const getStatusColor = (status?: string) => {
  switch (status) {
    case 'SUCCESS':
      return 'green';
    case 'FAILURE':
      return 'red';
    case 'BUILDING':
      return 'blue';
    default:
      return 'default';
  }
};

export default BuildMonitor;