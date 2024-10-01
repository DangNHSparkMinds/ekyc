import { ReactNode, useRef, useState } from "react";

interface FileInputProps {
  children: ReactNode;
  onChange: (file: any) => void;
}

const FileInput: React.FunctionComponent<FileInputProps> = (props) => {

  const inputRef = useRef<HTMLInputElement | null>(null);
  const [isDragging, setIsDragging] = useState(false);
  const [selectedFile, setSelectedFile] = useState<File | null>(null);

  // Trigger input click when div is clicked
  const handleDivClick = () => {
    if (inputRef.current) {
      inputRef.current.click(); // Kích hoạt sự kiện click của input
    }
  };

  // Handle file selection
  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      setSelectedFile(file);
    }
    props.onChange(file);
  };

  // Handle drag events
  const handleDragOver = (e: React.DragEvent<HTMLDivElement>) => {
    e.preventDefault();
    setIsDragging(true);
  };

  const handleDragLeave = () => {
    setIsDragging(false);
  };

  // Handle drop event
  const handleDrop = (e: React.DragEvent<HTMLDivElement>) => {
    e.preventDefault();
    setIsDragging(false);

    const file = e.dataTransfer.files?.[0];
    if (file) {
      setSelectedFile(file);
    }
  };

  return (
    <div>
      <input
        type="file"
        ref={inputRef}
        style={{ display: 'none' }}
        onChange={handleFileChange}
        accept="image/*"
      />
      <div
        onClick={handleDivClick}
        onDragOver={handleDragOver}
        onDragLeave={handleDragLeave}
        onDrop={handleDrop}
        className="id-card"
        style={{
          border: isDragging ? '2px solid' : '2px dashed',
        }}
      >
        {selectedFile ? (
          <img
            src={URL.createObjectURL(selectedFile)}
            alt="card-review"
            style={{ maxWidth: '100%', maxHeight: '100%' }}
          />
        ) : (
          props.children
        )}
      </div>
    </div>
  );
};

export default FileInput;