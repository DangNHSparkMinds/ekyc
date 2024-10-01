import kycApi from '@/api/kycApi';
import { useAppDispatch } from '@/hooks/common';
import { setKycDocumentData } from '@/redux/slice/kycSlide';
import { FaceLandmarker, FilesetResolver, FaceLandmarkerResult } from '@mediapipe/tasks-vision';
import React, { useEffect, useRef, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { RotateSpinner } from "react-spinners-kit"

interface IMessageGuide {
  type?: 'ERROR' | 'SUCCESS' | 'WARN';
  guide?: string;
}

const FaceLandmarkerVerification: React.FC = () => {
  const navigate = useNavigate();
  const dispatch = useAppDispatch();
  const [videoWidth, setVideoWidth] = useState(200);
  const [videoHeight, setVideoHeight] = useState(265.5);
  const [message, setMessage] = useState<IMessageGuide>();
  const [isSubmitting, setIsSubmitting] = useState<boolean>(false);
  const [faceLandmarker, setFaceLandmarker] = useState<FaceLandmarker | null>(null);
  const videoRef = useRef<HTMLVideoElement | null>(null);
  const canvasRef = useRef<HTMLCanvasElement | null>(null);

  let step = 'DETECT';
  let detectErrorTime: number = 0;
  let predictErrorTime: number = 0;
  let lastVideoTime = -1;

  // Initialize the FaceLandmarker with the model options
  const initializeFaceLandmarker = async () => {
    const filesetResolver = await FilesetResolver.forVisionTasks(
      "https://cdn.jsdelivr.net/npm/@mediapipe/tasks-vision@0.10.3/wasm"
    );
    const landmarker = await FaceLandmarker.createFromOptions(filesetResolver, {
      baseOptions: {
        modelAssetPath: "https://storage.googleapis.com/mediapipe-models/face_landmarker/face_landmarker/float16/1/face_landmarker.task",
        delegate: "GPU"
      },
      outputFaceBlendshapes: true,
      runningMode: "VIDEO",
      numFaces: 1
    });
    setFaceLandmarker(landmarker);
  };

  useEffect(() => {
    initializeFaceLandmarker();
    const timer = setTimeout(() => {
      startWebcam();
    }, 1000);
    return () => clearTimeout(timer);
  }, []);

  const handleCameraLoaded = () => {
    requestAnimationFrame(predictWebcam);
  };

  // Calculate the angle between points
  const radians = (a1: number, a2: number, b1: number, b2: number) => Math.atan2(b2 - a2, b1 - a1);

  // Check if face detection has errors (no face detected)
  const checkFaceError = (faceLandmarks: any[], currentTime: number) => {
    if (faceLandmarks.length === 0) {
      setMessage({ guide: "No face detected.", type: 'ERROR' });
      detectErrorTime = currentTime;
      return true;
    }
    return false;
  };

  // Check for multiple faces detected
  const checkMultipleFaces = (faceLandmarks: any[], currentTime: number) => {
    if (faceLandmarks.length > 1) {
      setMessage({ guide: 'The camera has detected multiple faces.', type: 'ERROR' });
      predictErrorTime = currentTime;
      return true;
    }
    return false;
  };

  // Check the orientation of the face
  const checkFaceOrientation = (landmarks: any, currentTime: number) => {
    if (landmarks[33] && landmarks[263] && landmarks[10] && landmarks[152]) {
      const angle = {
        roll: radians(landmarks[33].x, landmarks[33].y, landmarks[263].x, landmarks[263].y) * 100,
        yaw: radians(landmarks[33].x, landmarks[33].z, landmarks[263].x, landmarks[263].z) * 100,
        pitch: radians(landmarks[10].y, landmarks[10].z, landmarks[152].y, landmarks[152].z) * 100,
      };
      if (Math.abs(angle.yaw) > 20 || angle.pitch > 20 || angle.pitch < -10 || Math.abs(angle.roll) > 10) {
        setMessage({ guide: 'Align your face straight and looking forward.', type: 'WARN' });
        predictErrorTime = currentTime;
        return false;
      }
      setMessage({ guide: 'Hold your face steady', type: 'SUCCESS' });
      return true;
    }
    return false;
  };

  const submitKycFace = async () => {
    setMessage({ guide: 'Completed !', type: 'SUCCESS' })
    const canvas = document.createElement('canvas');
    if (videoRef.current) {
      canvas.width = videoRef.current.videoWidth;
      canvas.height = videoRef.current.videoHeight;
      const context = canvas.getContext('2d');
      if (context) {
        context.translate(canvas.width, 0);
        context.scale(-1, 1);
        context.drawImage(videoRef.current, 0, 0, canvas.width, canvas.height);
        const dataURL = canvas.toDataURL('image/png');
        const binary = atob(dataURL.split(',')[1]);
        const array = [];
        for (let i = 0; i < binary.length; i++) {
          array.push(binary.charCodeAt(i));
        }
        const frontFaceImage = new Blob([new Uint8Array(array)], { type: 'image/png' });
        setIsSubmitting(true);
        try {
          const reqBody = {
            file: frontFaceImage,
          }
          const response = await kycApi.verifyFace(reqBody);
          if (response.ok && response.body) {
            if (response.body.status === 'APPROVED') {
              dispatch(setKycDocumentData(response.body));
              navigate("/kyc/result");
            } else {

            }
            const stream = await navigator.mediaDevices.getUserMedia({ video: true });
            if (stream) {
              stream.getTracks().forEach(track => track.stop());
            }
          }
        } catch (err) {
          console.error('Unexpected error', err);
        } finally {
          setIsSubmitting(false);
          if (videoRef.current) {
            videoRef.current.srcObject = null;
          }
        }
      }
    }
  }

  // Main face analyzing function
  const analyzingFace = (results: FaceLandmarkerResult) => {
    const faceLandmarks = results.faceLandmarks;
    const currentTime = performance.now();

    // Check for errors and handle conditions
    if (checkFaceError(faceLandmarks, currentTime) || checkMultipleFaces(faceLandmarks, currentTime)) {
      return;
    }
    if (step === 'PREDICT') {
      const isFaceStable = checkFaceOrientation(faceLandmarks[0], currentTime);

      const isFirstTimeStable = predictErrorTime === 0 && currentTime > 7000;
      const isStableForEnoughTime = predictErrorTime !== 0 && (currentTime - predictErrorTime) > 2000;

      if (isFaceStable && (isFirstTimeStable || isStableForEnoughTime)) {
        videoRef.current?.pause();
        submitKycFace();
      }
    } else {
      setMessage({ guide: 'Looking forward', type: 'SUCCESS' });
    }
    if (currentTime - detectErrorTime > 2000) {
      step = 'PREDICT';
      setVideoWidth(350);
    } else {
      step = 'DETECT';
      setVideoWidth(200);
    }
  };

  const startWebcam = async () => {
    if (navigator.mediaDevices && navigator.mediaDevices.getUserMedia) {
      const stream = await navigator.mediaDevices.getUserMedia({ video: true });
      if (videoRef.current) {
        videoRef.current.srcObject = stream;
        videoRef.current.play();
      }
      if (canvasRef.current) {
        canvasRef.current.width = videoWidth;
        canvasRef.current.height = videoHeight;
      }
    }
  };

  const predictWebcam = () => {
    if (videoRef.current && canvasRef.current && faceLandmarker) {
      const videoElement = videoRef.current;

      if (videoElement.videoWidth > 0 && videoElement.videoHeight > 0) {
        const startTimeMs = performance.now();
        if (lastVideoTime !== videoElement.currentTime) {
          lastVideoTime = videoElement.currentTime;
          const results = faceLandmarker.detectForVideo(videoElement, startTimeMs);
          analyzingFace(results);
        }
      }
      requestAnimationFrame(predictWebcam);
    }
  };

  return (
    <>
      {/* <PushSpinner size={30} color="#686769" loading={loading} /> */}
      <div className="video-guide">
        <h5>{message?.guide}</h5>
      </div>
      <section id="demos">
        <div id="liveView" className="videoView">
          <div style={{ position: "relative" }} className="d-flex justify-content-center">
            <video
              ref={videoRef}
              id="webcam"
              width={videoWidth}
              height={videoHeight}
              autoPlay
              playsInline
              onLoadedMetadata={handleCameraLoaded}
              style={{ borderColor: message?.type === 'SUCCESS' ? "#F47A53" : "#b8b8b8" }}
            >
            </video>
            <canvas ref={canvasRef} id="output_canvas" style={{ position: "absolute", left: 0, top: 0, transform: "scale(-1, 1)" }}></canvas>
          </div>
        </div>
        <div className="loader-kyc">
          <RotateSpinner size={30} color="#f47a53" loading={isSubmitting} />
        </div>
      </section>
    </>
  );
};

export default FaceLandmarkerVerification;
