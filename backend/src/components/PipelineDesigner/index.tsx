import React, { useState } from 'react';
import { Card, Form, Input, Button, Space, message } from 'antd';
import { PlusOutlined, DeleteOutlined } from '@ant-design/icons';
import { StageForm } from './StageForm';
import { PipelineStage, PipelineStep } from './types';

const PipelineDesigner: React.FC = () => {
  const [form] = Form.useForm();
  const [stages, setStages] = useState<PipelineStage[]>([]);

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      const response = await fetch('/api/pipeline/create', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          name: values.name,
          stages: stages,
        }),
      });

      if (response.ok) {
        message.success('流水线创建成功');
      } else {
        message.error('流水线创建失败');
      }
    } catch (error) {
      message.error('表单验证失败');
    }
  };

  const addStage = () => {
    setStages([...stages, { name: '', steps: [] }]);
  };

  const updateStage = (index: number, stage: PipelineStage) => {
    const newStages = [...stages];
    newStages[index] = stage;
    setStages(newStages);
  };

  const removeStage = (index: number) => {
    setStages(stages.filter((_, i) => i !== index));
  };

  return (
    <Card title="流水线设计器">
      <Form form={form} layout="vertical">
        <Form.Item
          name="name"
          label="流水线名称"
          rules={[{ required: true, message: '请输入流水线名称' }]}
        >
          <Input placeholder="请输入流水线名称" />
        </Form.Item>

        {stages.map((stage, index) => (
          <StageForm
            key={index}
            stage={stage}
            onChange={(updatedStage) => updateStage(index, updatedStage)}
            onRemove={() => removeStage(index)}
          />
        ))}

        <Space direction="vertical" style={{ width: '100%' }}>
          <Button type="dashed" onClick={addStage} block icon={<PlusOutlined />}>
            添加阶段
          </Button>

          <Button type="primary" onClick={handleSubmit} block>
            创建流水线
          </Button>
        </Space>
      </Form>
    </Card>
  );
};

export default PipelineDesigner;