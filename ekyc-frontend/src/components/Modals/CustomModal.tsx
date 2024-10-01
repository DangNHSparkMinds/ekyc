import { CheckCircleOutlined, CloseCircleOutlined } from "@ant-design/icons";
import { Modal } from "antd";

interface ModalProps {
  title?: string;
  onOk?: any;
  onCancel?: any;
  message?: any;
  color?: string,
}

const CustomModal: React.FC<ModalProps> = (props) => {
  const { title, onOk, onCancel, message, color } = props;

  const checkIcon = () => {
    switch (color) {
      case "success":
        return <CheckCircleOutlined className="modal__success__icon" />
      default:
        return <CloseCircleOutlined className="modal__error__icon" />
    }
  }

  return (
    <>
        <Modal open={true} onOk={onOk} onCancel={onCancel} className="ant-modal" cancelButtonProps={{ style: { display: 'none' } }} okType="default">
          <div className="modal__title">{checkIcon()}{title}</div>
          <div className="modal__message">{message}</div>
        </Modal>
    </>
  );
};

export default CustomModal;
