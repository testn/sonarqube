/*
 * SonarQube
 * Copyright (C) 2009-2018 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
import * as React from 'react';
import DeferredSpinner from './DeferredSpinner';
import Modal from '../controls/Modal';
import { ResetButtonLink } from '../ui/buttons';
import { translate } from '../../helpers/l10n';
import WarningIcon from '../icons-components/WarningIcon';
import { getTask } from '../../api/ce';

interface Props {
  onClose: () => void;
  taskId: string;
}

interface State {
  loading: boolean;
  warnings: string[];
}

export default class AnalysisWarningsModal extends React.PureComponent<Props, State> {
  mounted = false;
  state: State = {
    loading: true,
    warnings: []
  };

  componentDidMount() {
    this.mounted = true;
    this.loadWarnings();
  }

  componentDidUpdate(prevProps: Props) {
    if (prevProps.taskId !== this.props.taskId) {
      this.loadWarnings();
    }
  }

  componentWillUnmount() {
    this.mounted = false;
  }

  loadWarnings() {
    this.setState({ loading: true });
    getTask(this.props.taskId, ['warnings']).then(
      ({ warnings = [] }) => {
        if (this.mounted) {
          this.setState({ loading: false, warnings });
        }
      },
      () => {
        if (this.mounted) {
          this.setState({ loading: false });
        }
      }
    );
  }

  render() {
    const header = translate('warnings');
    return (
      <Modal contentLabel={header} onRequestClose={this.props.onClose}>
        <header className="modal-head">
          <h2>{header}</h2>
        </header>

        <div className="modal-body js-analysis-warnings">
          <DeferredSpinner loading={this.state.loading}>
            {this.state.warnings.map((warning, index) => (
              <div className="panel panel-vertical" key={index}>
                <WarningIcon className="pull-left spacer-right" />
                <div className="overflow-hidden markdown">{warning}</div>
              </div>
            ))}
          </DeferredSpinner>
        </div>

        <footer className="modal-foot">
          <ResetButtonLink className="js-modal-close" onClick={this.props.onClose}>
            {translate('close')}
          </ResetButtonLink>
        </footer>
      </Modal>
    );
  }
}