import React from "react";
import { Link } from "react-router-dom";

const headerItems = [
  { name: 'Dashboard', link: '/dashboard' },
  { name: 'Statics', link: '/statics' },
  { name: 'My Wallet', link: '/my-wallet' },
  { name: 'Transfers', link: '/transfer' },
  { name: 'Messages', link: '/message' }
]

const MainHeader: React.FunctionComponent = () => {
  return (
    <div className="main-header">
      <img src="/src/assets/app.png" />
      <ul>
        {
          headerItems.map((item) => (
            <li>
              <Link to={item.link}>
                {item.name}
              </Link>
            </li>
          ))
        }
      </ul>
    </div>
  );
};

export default MainHeader;